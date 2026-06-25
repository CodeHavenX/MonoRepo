package com.cramsan

/**
 * Fails (or warns) when the build is triggered from an unsupported IDE or a version below the minimum.
 *
 * Detection relies on JVM system properties set by JetBrains IDEs:
 *   - `idea.active`           — `true` when running inside any IntelliJ-Platform IDE
 *   - `idea.platform.prefix`  — `AndroidStudio`, `Idea`, etc.
 *   - `idea.version`          — dot-separated marketing version, e.g. `2026.2` or `2026.3.1`
 *
 * Command-line `./gradlew` invocations (where `idea.active` is absent) are always skipped.
 * Non-JetBrains IDEs that do not set these properties are reported as "unknown".
 *
 * Configuration (in root build.gradle.kts):
 * ```kotlin
 * ideCheck {
 *     failOnUnsupportedIde.set(true)   // default; set false to warn instead of fail
 *     ides {
 *         register("AndroidStudio") { minVersion.set("2026.2") }
 *         register("Idea")          { minVersion.set("2026.3") }
 *     }
 * }
 * ```
 */
val ideCheck = extensions.create<IdeCheckExtension>("ideCheck")

/**
 * Infers an `idea.platform.prefix`-compatible value from other system properties when
 * `idea.platform.prefix` itself is absent (observed in Android Studio 2026.1+).
 *
 * Detection priority:
 * 1. `idea.vendor.name` — `"Google"` → `"AndroidStudio"`, `"JetBrains"` → `"Idea"`
 * 2. `idea.paths.selector` — leading word encodes the family: `"AndroidStudio2026.1"`,
 *    `"IdeaIC2026.3"`, etc.
 * 3. Presence of `android.studio.latest.known.compatible.agp.version` → `"AndroidStudio"`
 *
 * Returns `null` when none of the signals match a known IDE.
 */
fun inferPlatformPrefix(): String? {
    System.getProperty("idea.vendor.name")?.let { vendor ->
        return when (vendor) {
            "Google" -> "AndroidStudio"
            "JetBrains" -> "Idea"
            else -> null
        }
    }
    System.getProperty("idea.paths.selector")?.let { selector ->
        return when {
            selector.startsWith("AndroidStudio") -> "AndroidStudio"
            selector.startsWith("IdeaIC") || selector.startsWith("IntelliJIdea") -> "Idea"
            else -> null
        }
    }
    if (System.getProperty("android.studio.latest.known.compatible.agp.version") != null) {
        return "AndroidStudio"
    }
    return null
}

/**
 * Formats a user-facing IDE check violation as a speech bubble spoken by a cute cat,
 * followed by technical details. [title] is the one-line problem summary, [action] is
 * what the developer should do to fix it, and [details] are the raw property values.
 */
fun formatViolation(title: String, action: String, details: String): String {
    val bodyLines = listOf(
        "Problem : $title",
        "",
        "Fix     : $action",
        "",
        "To disable this check:",
        "  ideCheck { failOnUnsupportedIde.set(false) }",
        "  in the root build.gradle.kts",
    )
    val innerWidth = bodyLines.maxOf { it.length } + 2
    val bar = "-".repeat(innerWidth)
    fun row(text: String) = "  | ${text.padEnd(innerWidth - 1)}|"
    return buildString {
        appendLine()
        appendLine("  .$bar.")
        appendLine(row(""))
        bodyLines.forEach { appendLine(row(it)) }
        appendLine(row(""))
        appendLine("  `$bar'")
        appendLine("           \\")
        appendLine("            \\   /\\_/\\")
        appendLine("             \\ ( o.o )  < IDE Check Failed!")
        appendLine("               (> ^ <)")
        appendLine("                \"   \"")
        appendLine()
        appendLine("  Technical details:")
        details.lines().forEach { appendLine("    $it") }
    }
}

/** Compares two dot-separated version strings component by component. */
fun compareVersions(detected: String, minimum: String): Int {
    val detectedParts = detected.split(".").map { it.toIntOrNull() ?: 0 }
    val minimumParts = minimum.split(".").map { it.toIntOrNull() ?: 0 }
    val maxLen = maxOf(detectedParts.size, minimumParts.size)
    for (i in 0 until maxLen) {
        val d = detectedParts.getOrElse(i) { 0 }
        val m = minimumParts.getOrElse(i) { 0 }
        if (d != m) return d.compareTo(m)
    }
    return 0
}

gradle.projectsEvaluated {
    val isIdeActive = System.getProperty("idea.active")?.toBoolean() ?: false
    if (!isIdeActive) return@projectsEvaluated

    // Collect all IDE-related system properties and write them to a diagnostics file so they
    // can be inspected after a sync (terminal runs don't receive the IDE-injected properties).
    val ideProperties = System.getProperties()
        .entries
        .filter { (k, _) ->
            val key = k.toString()
            key.startsWith("idea.") || key.startsWith("android.") || key.startsWith("studio.")
        }
        .sortedBy { (k, _) -> k.toString() }
        .joinToString("\n") { (k, v) -> "$k=$v" }

    val diagnosticsFile = layout.buildDirectory.file("ide-check-diagnostics.txt").get().asFile
    diagnosticsFile.parentFile.mkdirs()
    diagnosticsFile.writeText(ideProperties)

    val ideVersion = System.getProperty("idea.version").orEmpty()
    val shouldFail = ideCheck.failOnUnsupportedIde.get()

    // `idea.platform.prefix` is the primary signal but may be absent in newer IDE versions.
    // Fall back to other properties that reliably identify the IDE family.
    val platformPrefix = System.getProperty("idea.platform.prefix") ?: inferPlatformPrefix()

    fun report(message: String) {
        if (shouldFail) throw GradleException(message) else logger.warn(message)
    }

    if (platformPrefix == null) {
        report(
            formatViolation(
                title = "Your IDE could not be identified.",
                action = "Add your IDE to the ideCheck block in the root build.gradle.kts.",
                details = buildString {
                    appendLine("idea.platform.prefix : (not set)")
                    appendLine("idea.paths.selector  : ${System.getProperty("idea.paths.selector") ?: "(not set)"}")
                    appendLine("idea.version         : ${ideVersion.ifEmpty { "(not set)" }}")
                    append("All IDE properties   : ${diagnosticsFile.absolutePath}")
                },
            )
        )
        return@projectsEvaluated
    }

    val spec = ideCheck.ides.findByName(platformPrefix)

    if (spec == null) {
        val supported = ideCheck.ides.names.joinToString(", ").ifEmpty { "(none configured)" }
        report(
            formatViolation(
                title = "\"$platformPrefix\" is not on the supported IDE list.",
                action = "Switch to one of the supported IDEs: $supported.",
                details = buildString {
                    appendLine("Detected : $platformPrefix${if (ideVersion.isNotEmpty()) " $ideVersion" else ""}")
                    append("Supported: $supported")
                },
            )
        )
        return@projectsEvaluated
    }

    val minVersion = spec.minVersion.orNull
    if (minVersion != null && ideVersion.isNotEmpty() && compareVersions(ideVersion, minVersion) < 0) {
        report(
            formatViolation(
                title = "Your $platformPrefix $ideVersion is below the minimum required version.",
                action = "Upgrade $platformPrefix to $minVersion or newer.",
                details = buildString {
                    appendLine("Detected : $platformPrefix $ideVersion")
                    append("Minimum  : $platformPrefix $minVersion")
                },
            )
        )
    }
}

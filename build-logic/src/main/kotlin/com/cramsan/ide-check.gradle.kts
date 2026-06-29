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
 *
 * The detection and plugin-resolution logic lives in [IdeCheckUtils.kt] so it can be unit-tested
 * independently of Gradle.
 */
val ideCheck = extensions.create<IdeCheckExtension>("ideCheck")

/**
 * Formats a user-facing IDE check violation as a speech bubble spoken by a cute cat,
 * followed by technical details. [title] is the one-line problem summary, [action] is
 * what the developer should do to fix it, [details] are the raw property values, and
 * [disableProperty] is the `ideCheck` property the developer can set to `false` to
 * downgrade this violation to a warning.
 */
fun formatViolation(
    title: String,
    action: String,
    details: String,
    disableProperty: String = "failOnUnsupportedIde",
): String {
    val bodyLines = listOf(
        "Problem : $title",
        "",
        "Fix     : $action",
        "",
        "To disable this check:",
        "  ideCheck { $disableProperty.set(false) }",
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

gradle.projectsEvaluated {
    val isIdeActive = System.getProperty("idea.active")?.toBoolean() ?: false
    if (!isIdeActive) return@projectsEvaluated

    // Snapshot all system properties into a plain Map once so the utility functions
    // (which live in IdeCheckUtils.kt and are unit-tested independently) receive
    // pure data rather than live System calls.
    val sysProps: Map<String, String> = System.getProperties()
        .entries
        .associate { (k, v) -> k.toString() to v.toString() }

    // Collect all IDE-related system properties and write them to a diagnostics file so they
    // can be inspected after a sync (terminal runs don't receive the IDE-injected properties).
    val ideProperties = sysProps.entries
        .filter { (k, _) -> k.startsWith("idea.") || k.startsWith("android.") || k.startsWith("studio.") }
        .sortedBy { (k, _) -> k }
        .joinToString("\n") { (k, v) -> "$k=$v" }

    val diagnosticsFile = layout.buildDirectory.file("ide-check-diagnostics.txt").get().asFile
    diagnosticsFile.parentFile.mkdirs()
    diagnosticsFile.writeText(ideProperties)

    val ideVersion = System.getProperty("idea.version").orEmpty()
    val shouldFail = ideCheck.failOnUnsupportedIde.get()

    // `idea.platform.prefix` is the primary signal but may be absent in newer IDE versions.
    // Fall back to other properties that reliably identify the IDE family.
    val platformPrefix = System.getProperty("idea.platform.prefix") ?: inferPlatformPrefix(sysProps)

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
        val otherIdes = ideCheck.ides.filter { it.name != platformPrefix }
        val alternativesInline = otherIdes.joinToString(", ") { ide ->
            val min = ide.minVersion.orNull
            if (min != null) "${ide.name} (>=$min)" else ide.name
        }
        val actionFix = buildString {
            append("Upgrade $platformPrefix to $minVersion or newer.")
            if (otherIdes.isNotEmpty()) append(" Or use: $alternativesInline.")
        }
        report(
            formatViolation(
                title = "Your $platformPrefix $ideVersion is below the minimum required version.",
                action = actionFix,
                details = buildString {
                    appendLine("Detected : $platformPrefix $ideVersion")
                    appendLine("Minimum  : $platformPrefix $minVersion")
                    if (otherIdes.isNotEmpty()) {
                        appendLine()
                        appendLine("Other supported IDEs:")
                        otherIdes.forEach { ide ->
                            val min = ide.minVersion.orNull
                            appendLine("  ${ide.name}: ${if (min != null) "$min or newer" else "any version"}")
                        }
                    }
                },
            )
        )
    }

    // --- Plugin presence check ---
    val shouldFailOnPlugin = ideCheck.failOnMissingPlugin.get()

    fun reportPlugin(message: String) {
        if (shouldFailOnPlugin) throw GradleException(message) else logger.warn(message)
    }

    val pluginDirs = resolvePluginDirs(
        userHome = System.getProperty("user.home").orEmpty(),
        osName = System.getProperty("os.name").orEmpty(),
        properties = sysProps,
        env = System.getenv(),
    )
    val requiredPlugins = ideCheck.requiredPlugins.toList()

    if (requiredPlugins.isNotEmpty()) {
        val missing = requiredPlugins.filter { pluginSpec ->
            val dir = pluginSpec.dirName.orNull
            if (dir == null) {
                logger.warn("ide-check: plugin '${pluginSpec.name}' has no dirName set — skipping.")
                false
            } else {
                !isPluginInstalled(dir, pluginDirs)
            }
        }

        if (missing.isNotEmpty()) {
            val missingList = missing.joinToString(", ") { it.name }
            reportPlugin(
                formatViolation(
                    title = "Required IDE plugin(s) not found: $missingList.",
                    action = "Install them via Settings → Plugins or the JetBrains Marketplace.",
                    disableProperty = "failOnMissingPlugin",
                    details = buildString {
                        appendLine("Searched directories:")
                        if (pluginDirs.isEmpty()) {
                            appendLine("  (none found — idea.plugins.path, idea.config.path, idea.home.path may be unset)")
                        } else {
                            pluginDirs.forEach { appendLine("  ${it.absolutePath}") }
                        }
                        appendLine()
                        appendLine("Missing plugins (display name → expected dir name):")
                        missing.forEach { appendLine("  ${it.name} → ${it.dirName.orNull ?: "(unset)"}") }
                    },
                )
            )
        }
    }
}

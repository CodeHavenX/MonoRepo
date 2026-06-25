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

/**
 * Returns every directory that an IntelliJ-platform IDE uses to store plugins.
 *
 * The primary signals are `idea.vendor.name` ("Google" / "JetBrains") and `idea.version`
 * ("2026.1"), which are reliably injected even by IDE versions that set `idea.config.path`
 * to a fake placeholder (observed in Android Studio 2026.1+). Four locations are probed:
 *
 * 1. `idea.plugins.path` / `idea.home.path` — explicit system properties when available.
 * 2. `idea.config.path` (only when it is an absolute path) — traditional `<config>/plugins/`
 *    layout (Windows / macOS), plus XDG data dir on Linux.
 * 3. OS-specific vendor data dir scan — the vendor directory is scanned for subdirectories
 *    whose name starts with the IDE family prefix + `idea.version` (e.g. `AndroidStudio2026.1`).
 *    The base dir and whether to append `plugins/` differ by OS:
 *      - Linux:   `~/.local/share/<vendor>/`              plugins are directly in selector dir
 *      - macOS:   `~/Library/Application Support/<vendor>/`  plugins are in selector/plugins/
 *      - Windows: `%APPDATA%\<vendor>\`                   plugins are in selector\plugins\
 * 4. JetBrains Toolbox bundled plugins — `<toolbox-apps>/<app>/plugins/` matched by
 *    `dataDirectoryName` prefix. Toolbox root is OS-specific:
 *    `%LOCALAPPDATA%\JetBrains\Toolbox\apps` (Windows),
 *    `~/Library/Application Support/JetBrains/Toolbox/apps` (macOS),
 *    `~/.local/share/JetBrains/Toolbox/apps` (Linux).
 * 5. IDE application bundle — plugins bundled inside the installed IDE application itself:
 *    `~/Applications/Android Studio*.app/Contents/plugins/` or `/Applications/…` (macOS),
 *    `%PROGRAMFILES%\<vendor>\<app>\plugins\` (Windows),
 *    `/opt/<ide>/plugins/` or `/usr/local/<ide>/plugins/` (Linux).
 */
fun resolvePluginDirs(): List<java.io.File> {
    val dirs = mutableListOf<java.io.File>()
    val userHome = System.getProperty("user.home") ?: return dirs
    val osName   = System.getProperty("os.name").orEmpty()
    val isWindows = osName.startsWith("Windows")
    val isMac     = osName.contains("Mac")

    // 1. Explicit system properties (highest priority, not always injected)
    System.getProperty("idea.plugins.path")?.let { dirs.add(java.io.File(it)) }
    System.getProperty("idea.home.path")?.let { dirs.add(java.io.File(it, "plugins")) }

    // 2. idea.config.path — only trusted when it resolves to an absolute path.
    //    Some IDE versions inject a fake relative placeholder (e.g. "some/non/existent/path").
    val configFile = System.getProperty("idea.config.path")?.let { java.io.File(it) }
    if (configFile != null && configFile.isAbsolute) {
        dirs.add(java.io.File(configFile, "plugins"))

        // XDG layout (Linux only): user plugins live directly in <xdg-data>/<vendor>/<selector>/
        if (!isWindows && !isMac) {
            val vendor    = configFile.parentFile?.name
            val xdgConfig = java.io.File(userHome, ".config")
            val xdgData   = System.getenv("XDG_DATA_HOME")?.let { java.io.File(it) }
                ?: java.io.File(userHome, ".local/share")
            if (vendor != null && configFile.canonicalPath
                    .startsWith(xdgConfig.canonicalPath + java.io.File.separator)) {
                dirs.add(java.io.File(xdgData, "$vendor/${configFile.name}"))
            }
        }
    }

    // Build an IDE family prefix from vendor + version for fallback discovery.
    // "Google" + "2026.1" → "AndroidStudio2026.1"  (matches 2026.1.1, 2026.1.2, …)
    val vendorName   = System.getProperty("idea.vendor.name")
    val ideVersion   = System.getProperty("idea.version").orEmpty()
    val familyPrefix = when {
        vendorName == "Google"    && ideVersion.isNotEmpty() -> "AndroidStudio$ideVersion"
        vendorName == "JetBrains" && ideVersion.isNotEmpty() -> "IdeaIC$ideVersion"
        else -> null
    }

    if (familyPrefix != null) {
        // 3. OS-specific vendor data dir scan for user-installed plugin dirs.
        //    Layout differences by OS:
        //      Linux   — plugins are a direct child of the selector dir (no /plugins/ subdir)
        //      macOS   — plugins are in <selector>/plugins/
        //      Windows — plugins are in <selector>\plugins\
        val vendorDataDir: java.io.File? = when {
            isWindows -> {
                val appData = System.getenv("APPDATA") ?: java.io.File(userHome, "AppData/Roaming").path
                when (vendorName) {
                    "Google"    -> java.io.File(appData, "Google")
                    "JetBrains" -> java.io.File(appData, "JetBrains")
                    else        -> null
                }
            }
            isMac -> when (vendorName) {
                "Google"    -> java.io.File(userHome, "Library/Application Support/Google")
                "JetBrains" -> java.io.File(userHome, "Library/Application Support/JetBrains")
                else        -> null
            }
            else -> {
                val xdgData = System.getenv("XDG_DATA_HOME")?.let { java.io.File(it) }
                    ?: java.io.File(userHome, ".local/share")
                when (vendorName) {
                    "Google"    -> java.io.File(xdgData, "Google")
                    "JetBrains" -> java.io.File(xdgData, "JetBrains")
                    else        -> null
                }
            }
        }
        vendorDataDir?.listFiles()
            ?.filter { it.isDirectory && it.name.startsWith(familyPrefix) }
            ?.forEach { selectorDir ->
                // Linux: plugins live directly in the selector dir itself
                // Windows / macOS: plugins live in <selector>/plugins/
                if (isWindows || isMac) dirs.add(java.io.File(selectorDir, "plugins"))
                else                    dirs.add(selectorDir)
            }

        // 4. Toolbox bundled plugins: prefix-match dataDirectoryName in product-info.json.
        val toolboxApps = when {
            isWindows ->
                java.io.File(System.getenv("LOCALAPPDATA") ?: java.io.File(userHome, "AppData/Local").path, "JetBrains/Toolbox/apps")
            isMac ->
                java.io.File(userHome, "Library/Application Support/JetBrains/Toolbox/apps")
            else ->
                java.io.File(userHome, ".local/share/JetBrains/Toolbox/apps")
        }
        toolboxApps.listFiles()?.forEach { appDir ->
            val productInfo = java.io.File(appDir, "product-info.json")
            if (productInfo.isFile) {
                val content = productInfo.readText()
                // Prefix-match so "AndroidStudio2026.1" matches "AndroidStudio2026.1.1", etc.
                // Two spacing variants cover standard pretty-print and compact JSON.
                if (content.contains("\"dataDirectoryName\": \"$familyPrefix") ||
                    content.contains("\"dataDirectoryName\":\"$familyPrefix")) {
                    dirs.add(java.io.File(appDir, "plugins"))
                }
            }
        }

        // 5. IDE application bundle — bundled plugins ship inside the installed app itself.
        //    macOS:   ~/Applications/<App>.app/Contents/plugins/ or /Applications/<App>.app/Contents/plugins/
        //    Linux:   /opt/<ide>/plugins/ or /usr/local/<ide>/plugins/ or ~/.<ide>/plugins/
        //    Windows: %PROGRAMFILES%\<vendor>\<app>\plugins\
        val appBundleName = when (vendorName) {
            "Google"    -> "Android Studio"
            "JetBrains" -> "IntelliJ IDEA"
            else        -> null
        }
        if (appBundleName != null) {
            if (isMac) {
                val appDirs = listOf(
                    java.io.File(userHome, "Applications"),
                    java.io.File("/Applications"),
                )
                for (appParent in appDirs) {
                    appParent.listFiles()
                        ?.filter { it.name.startsWith(appBundleName) && it.name.endsWith(".app") }
                        ?.forEach { appBundle ->
                            dirs.add(java.io.File(appBundle, "Contents/plugins"))
                        }
                }
            } else if (isWindows) {
                val programFiles = System.getenv("PROGRAMFILES") ?: "C:\\Program Files"
                val winVendor = if (vendorName == "Google") "Google" else "JetBrains"
                java.io.File(programFiles, winVendor).listFiles()
                    ?.filter { it.isDirectory && it.name.startsWith(appBundleName) }
                    ?.forEach { dirs.add(java.io.File(it, "plugins")) }
            } else {
                val linuxDirName = if (vendorName == "Google") "android-studio" else "idea"
                listOf(
                    java.io.File("/opt/$linuxDirName/plugins"),
                    java.io.File("/usr/local/$linuxDirName/plugins"),
                    java.io.File("/snap/$linuxDirName/current/plugins"),
                ).forEach { dirs.add(it) }
            }
        }
    }

    return dirs.filter { it.exists() && it.isDirectory }.distinct()
}

/** Returns `true` when a subdirectory named [dirName] exists in any of [pluginDirs]. */
fun isPluginInstalled(dirName: String, pluginDirs: List<java.io.File>): Boolean =
    pluginDirs.any { it.resolve(dirName).exists() }

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

    // --- Plugin presence check ---
    val shouldFailOnPlugin = ideCheck.failOnMissingPlugin.get()

    fun reportPlugin(message: String) {
        if (shouldFailOnPlugin) throw GradleException(message) else logger.warn(message)
    }

    val pluginDirs = resolvePluginDirs()
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

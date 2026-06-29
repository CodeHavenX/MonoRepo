package com.cramsan

/**
 * Pure utility functions for IDE detection and plugin resolution used by [ide-check.gradle.kts].
 *
 * All functions are parameterized (no direct calls to [System.getProperty] or [System.getenv])
 * so they can be unit-tested without running a Gradle build.
 */

/**
 * Infers an `idea.platform.prefix`-compatible value from [properties] when
 * `idea.platform.prefix` itself is absent (observed in Android Studio 2026.1+).
 *
 * Detection priority:
 * 1. `idea.vendor.name` — `"Google"` → `"AndroidStudio"`, `"JetBrains"` → `"Idea"`
 * 2. `idea.paths.selector` — leading word encodes the family
 * 3. Presence of `android.studio.latest.known.compatible.agp.version` → `"AndroidStudio"`
 *
 * Returns `null` when none of the signals match a known IDE.
 */
fun inferPlatformPrefix(properties: Map<String, String>): String? {
    properties["idea.vendor.name"]?.let { vendor ->
        return when (vendor) {
            "Google" -> "AndroidStudio"
            "JetBrains" -> "Idea"
            else -> null
        }
    }
    properties["idea.paths.selector"]?.let { selector ->
        return when {
            selector.startsWith("AndroidStudio") -> "AndroidStudio"
            selector.startsWith("IdeaIC") || selector.startsWith("IntelliJIdea") -> "Idea"
            else -> null
        }
    }
    if (properties.containsKey("android.studio.latest.known.compatible.agp.version")) {
        return "AndroidStudio"
    }
    return null
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

/** Returns `true` when a subdirectory named [dirName] exists in any of [pluginDirs]. */
fun isPluginInstalled(dirName: String, pluginDirs: List<java.io.File>): Boolean =
    pluginDirs.any { it.resolve(dirName).exists() }

/**
 * Returns every directory that an IntelliJ-platform IDE uses to store plugins.
 *
 * [userHome] and [osName] replace the `user.home` / `os.name` system properties.
 * [properties] supplies IDE-specific properties (`idea.*`, `android.*`, etc.).
 * [env] supplies environment variables (`XDG_DATA_HOME`, `APPDATA`, etc.).
 *
 * Five locations are probed (see [ide-check.gradle.kts] for full documentation).
 *
 * For JetBrains IDEs, both the Community (`IdeaIC*`) and Ultimate (`IntelliJIdea*`)
 * directory-name prefixes are tried, because the edition cannot be determined reliably
 * from system properties alone.
 *
 * The version is truncated to major.minor before building directory prefixes because
 * IDE data directories use only major.minor (e.g. `IntelliJIdea2026.1`) even when
 * `idea.version` reports a patch component (e.g. `2026.1.3`).
 */
fun resolvePluginDirs(
    userHome: String,
    osName: String,
    properties: Map<String, String>,
    env: Map<String, String>,
): List<java.io.File> {
    val dirs = mutableListOf<java.io.File>()
    val isWindows = osName.startsWith("Windows")
    val isMac = osName.contains("Mac")

    // 1. Explicit system properties (highest priority, not always injected)
    properties["idea.plugins.path"]?.let { dirs.add(java.io.File(it)) }
    properties["idea.home.path"]?.let { dirs.add(java.io.File(it, "plugins")) }

    // 2. idea.config.path — only trusted when it resolves to an absolute path.
    //    Some IDE versions inject a fake relative placeholder (e.g. "some/non/existent/path").
    val configFile = properties["idea.config.path"]?.let { java.io.File(it) }
    if (configFile != null && configFile.isAbsolute) {
        dirs.add(java.io.File(configFile, "plugins"))

        // XDG layout (Linux only): user plugins live directly in <xdg-data>/<vendor>/<selector>/
        if (!isWindows && !isMac) {
            val vendor = configFile.parentFile?.name
            val xdgConfig = java.io.File(userHome, ".config")
            val xdgData = env["XDG_DATA_HOME"]?.let { java.io.File(it) }
                ?: java.io.File(userHome, ".local/share")
            if (vendor != null && configFile.canonicalPath
                    .startsWith(xdgConfig.canonicalPath + java.io.File.separator)
            ) {
                dirs.add(java.io.File(xdgData, "$vendor/${configFile.name}"))
            }
        }
    }

    // Build IDE family prefixes.
    // Directory names always use only major.minor (e.g. "IntelliJIdea2026.1") even when
    // idea.version includes a patch component (e.g. "2026.1.3"), so we truncate here.
    // JetBrains Community = IdeaIC*, Ultimate = IntelliJIdea* — both are tried.
    val vendorName = properties["idea.vendor.name"]
    val ideVersion = properties["idea.version"].orEmpty()
    val majorMinorVersion = ideVersion.split(".").take(2).joinToString(".")
    val familyPrefixes: List<String> = when {
        vendorName == "Google"    && ideVersion.isNotEmpty() -> listOf("AndroidStudio$majorMinorVersion")
        vendorName == "JetBrains" && ideVersion.isNotEmpty() -> listOf("IdeaIC$majorMinorVersion", "IntelliJIdea$majorMinorVersion")
        else -> emptyList()
    }

    if (familyPrefixes.isNotEmpty()) {
        // 3. OS-specific vendor data dir scan for user-installed plugin dirs.
        //    Layout differences by OS:
        //      Linux   — plugins are a direct child of the selector dir (no /plugins/ subdir)
        //      macOS   — plugins are in <selector>/plugins/
        //      Windows — plugins are in <selector>\plugins\
        val vendorDataDir: java.io.File? = when {
            isWindows -> {
                val appData = env["APPDATA"] ?: java.io.File(userHome, "AppData/Roaming").path
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
                val xdgData = env["XDG_DATA_HOME"]?.let { java.io.File(it) }
                    ?: java.io.File(userHome, ".local/share")
                when (vendorName) {
                    "Google"    -> java.io.File(xdgData, "Google")
                    "JetBrains" -> java.io.File(xdgData, "JetBrains")
                    else        -> null
                }
            }
        }
        vendorDataDir?.listFiles()
            ?.filter { it.isDirectory && familyPrefixes.any { prefix -> it.name.startsWith(prefix) } }
            ?.forEach { selectorDir ->
                // Linux: plugins live directly in the selector dir itself
                // Windows / macOS: plugins live in <selector>/plugins/
                if (isWindows || isMac) dirs.add(java.io.File(selectorDir, "plugins"))
                else                    dirs.add(selectorDir)
            }

        // 4. Toolbox bundled plugins: prefix-match dataDirectoryName in product-info.json.
        //    Both IdeaIC (Community) and IntelliJIdea (Ultimate) prefixes are checked.
        val toolboxApps = when {
            isWindows ->
                java.io.File(env["LOCALAPPDATA"] ?: java.io.File(userHome, "AppData/Local").path, "JetBrains/Toolbox/apps")
            isMac ->
                java.io.File(userHome, "Library/Application Support/JetBrains/Toolbox/apps")
            else ->
                java.io.File(userHome, ".local/share/JetBrains/Toolbox/apps")
        }
        toolboxApps.listFiles()?.forEach { appDir ->
            val productInfo = java.io.File(appDir, "product-info.json")
            if (productInfo.isFile) {
                val content = productInfo.readText()
                // Prefix-match so "IntelliJIdea2026.1" matches "IntelliJIdea2026.1.1", etc.
                // Two spacing variants cover standard pretty-print and compact JSON.
                if (familyPrefixes.any { prefix ->
                        content.contains("\"dataDirectoryName\": \"$prefix") ||
                        content.contains("\"dataDirectoryName\":\"$prefix")
                    }) {
                    dirs.add(java.io.File(appDir, "plugins"))
                }
            }
        }

        // 5. IDE application bundle — bundled plugins ship inside the installed app itself.
        //    macOS:   ~/Applications/<App>.app/Contents/plugins/ or /Applications/<App>.app/Contents/plugins/
        //    Linux:   /opt/<ide>/plugins/ or /usr/local/<ide>/plugins/ or /snap/<ide>/current/plugins/
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
                val programFiles = env["PROGRAMFILES"] ?: "C:\\Program Files"
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

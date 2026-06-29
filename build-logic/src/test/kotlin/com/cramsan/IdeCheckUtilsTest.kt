package com.cramsan

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

/**
 * Tests for [IdeCheckUtils] covering all three OSes (Linux, macOS, Windows) and the five
 * plugin-directory sources.  OS behaviour is simulated by passing different [osName] values —
 * the file-system operations all run against a JUnit [@TempDir], so the tests are portable.
 *
 * Sources probed by [resolvePluginDirs]:
 *   1. Explicit `idea.plugins.path` / `idea.home.path` system properties
 *   2. `idea.config.path` when absolute (+ XDG data dir on Linux)
 *   3. OS-specific vendor data dir scan (user-installed plugins)
 *        Linux   — plugins directly in selector dir (`~/.local/share/<vendor>/<selector>/`)
 *        macOS   — plugins in `selector/plugins/` (`~/Library/Application Support/<vendor>/<selector>/plugins/`)
 *        Windows — plugins in `selector\plugins\` (`%APPDATA%\<vendor>\<selector>\plugins\`)
 *   4. JetBrains Toolbox bundled plugins (`<toolbox-apps>/<app>/plugins/`)
 *   5. IDE application bundle (macOS `*.app`, Windows `%PROGRAMFILES%`, Linux `/opt/`)
 */
class IdeCheckUtilsTest {

    // =========================================================================
    // inferPlatformPrefix
    // =========================================================================

    @Test fun `inferPlatformPrefix returns AndroidStudio for Google vendor`() {
        assertEquals("AndroidStudio", inferPlatformPrefix(mapOf("idea.vendor.name" to "Google")))
    }

    @Test fun `inferPlatformPrefix returns Idea for JetBrains vendor`() {
        assertEquals("Idea", inferPlatformPrefix(mapOf("idea.vendor.name" to "JetBrains")))
    }

    @Test fun `inferPlatformPrefix vendor takes priority over android studio property`() {
        // JetBrains IDEA can have the Android plugin installed; the vendor name wins.
        val props = mapOf(
            "idea.vendor.name" to "JetBrains",
            "android.studio.latest.known.compatible.agp.version" to "9.0.0",
        )
        assertEquals("Idea", inferPlatformPrefix(props))
    }

    @Test fun `inferPlatformPrefix returns AndroidStudio from paths selector`() {
        assertEquals(
            "AndroidStudio",
            inferPlatformPrefix(mapOf("idea.paths.selector" to "AndroidStudio2026.1")),
        )
    }

    @Test fun `inferPlatformPrefix returns Idea from IdeaIC paths selector`() {
        assertEquals("Idea", inferPlatformPrefix(mapOf("idea.paths.selector" to "IdeaIC2026.1")))
    }

    @Test fun `inferPlatformPrefix returns Idea from IntelliJIdea paths selector`() {
        assertEquals(
            "Idea",
            inferPlatformPrefix(mapOf("idea.paths.selector" to "IntelliJIdea2026.1")),
        )
    }

    @Test fun `inferPlatformPrefix returns null for unrecognised paths selector`() {
        // A third-party IDE with an unknown selector name must not be misidentified.
        assertNull(inferPlatformPrefix(mapOf("idea.paths.selector" to "SomeOtherIDE2026.1")))
    }

    @Test fun `inferPlatformPrefix returns AndroidStudio from AGP version property alone`() {
        assertEquals(
            "AndroidStudio",
            inferPlatformPrefix(
                mapOf("android.studio.latest.known.compatible.agp.version" to "9.0.0"),
            ),
        )
    }

    @Test fun `inferPlatformPrefix returns null when no signals match`() {
        assertNull(inferPlatformPrefix(mapOf("idea.vendor.name" to "SomeOtherVendor")))
    }

    @Test fun `inferPlatformPrefix returns null for empty properties`() {
        assertNull(inferPlatformPrefix(emptyMap()))
    }

    // =========================================================================
    // compareVersions
    // =========================================================================

    @Test fun `compareVersions returns zero for equal versions`() {
        assertEquals(0, compareVersions("2026.1", "2026.1"))
    }

    @Test fun `compareVersions returns positive when detected is newer minor`() {
        assertTrue(compareVersions("2026.2", "2026.1") > 0)
    }

    @Test fun `compareVersions returns negative when detected is older minor`() {
        assertTrue(compareVersions("2026.1", "2026.2") < 0)
    }

    @Test fun `compareVersions returns positive when detected has patch and minimum does not`() {
        assertTrue(compareVersions("2026.1.3", "2026.1") > 0)
    }

    @Test fun `compareVersions returns negative when detected lacks patch that minimum requires`() {
        assertTrue(compareVersions("2026.1", "2026.1.3") < 0)
    }

    @Test fun `compareVersions returns positive when detected has newer major`() {
        assertTrue(compareVersions("2027.1", "2026.9") > 0)
    }

    @Test fun `compareVersions returns zero for equal three-component versions`() {
        assertEquals(0, compareVersions("2026.1.3", "2026.1.3"))
    }

    // =========================================================================
    // isPluginInstalled
    // =========================================================================

    @Test fun `isPluginInstalled returns true when plugin dir exists`(@TempDir root: File) {
        val pluginsDir = File(root, "plugins").also { it.mkdirs() }
        File(pluginsDir, "android").mkdirs()
        assertTrue(isPluginInstalled("android", listOf(pluginsDir)))
    }

    @Test fun `isPluginInstalled returns false when plugin dir is absent`(@TempDir root: File) {
        val pluginsDir = File(root, "plugins").also { it.mkdirs() }
        assertFalse(isPluginInstalled("android", listOf(pluginsDir)))
    }

    @Test fun `isPluginInstalled returns true when plugin is in second search dir`(@TempDir root: File) {
        val dir1 = File(root, "plugins1").also { it.mkdirs() }
        val dir2 = File(root, "plugins2").also { it.mkdirs() }
        File(dir2, "android").mkdirs()
        assertTrue(isPluginInstalled("android", listOf(dir1, dir2)))
    }

    @Test fun `isPluginInstalled returns false for empty plugin dirs list`() {
        assertFalse(isPluginInstalled("android", emptyList()))
    }

    // =========================================================================
    // resolvePluginDirs — source 1: explicit system properties
    // =========================================================================

    @Test fun `resolvePluginDirs returns idea plugins path when set`(@TempDir root: File) {
        val pluginsDir = File(root, "my-plugins").also { it.mkdirs() }
        val dirs = resolvePluginDirs(
            userHome = root.path,
            osName = "Linux",
            properties = mapOf("idea.plugins.path" to pluginsDir.path),
            env = emptyMap(),
        )
        assertTrue(dirs.contains(pluginsDir), "Expected $pluginsDir in $dirs")
    }

    @Test fun `resolvePluginDirs returns idea home plugins when set`(@TempDir root: File) {
        val ideHome = File(root, "ide-home").also { it.mkdirs() }
        val pluginsDir = File(ideHome, "plugins").also { it.mkdirs() }
        val dirs = resolvePluginDirs(
            userHome = root.path,
            osName = "Linux",
            properties = mapOf("idea.home.path" to ideHome.path),
            env = emptyMap(),
        )
        assertTrue(dirs.contains(pluginsDir), "Expected $pluginsDir in $dirs")
    }

    @Test fun `resolvePluginDirs includes both plugins path and home path when both set`(@TempDir root: File) {
        val pluginsDir = File(root, "explicit-plugins").also { it.mkdirs() }
        val homePluginsDir = File(root, "ide-home/plugins").also { it.mkdirs() }
        val dirs = resolvePluginDirs(
            userHome = root.path,
            osName = "Linux",
            properties = mapOf(
                "idea.plugins.path" to pluginsDir.path,
                "idea.home.path" to File(root, "ide-home").path,
            ),
            env = emptyMap(),
        )
        assertTrue(dirs.contains(pluginsDir), "Expected plugins.path dir in $dirs")
        assertTrue(dirs.contains(homePluginsDir), "Expected home/plugins dir in $dirs")
    }

    @Test fun `resolvePluginDirs filters out non-existent idea plugins path`(@TempDir root: File) {
        val dirs = resolvePluginDirs(
            userHome = root.path,
            osName = "Linux",
            properties = mapOf("idea.plugins.path" to File(root, "does-not-exist").path),
            env = emptyMap(),
        )
        assertTrue(dirs.isEmpty(), "Non-existent plugins.path must be filtered out, but dirs=$dirs")
    }

    // =========================================================================
    // resolvePluginDirs — source 2: idea.config.path
    // =========================================================================

    @Test fun `resolvePluginDirs ignores relative idea config path`(@TempDir root: File) {
        // Fake placeholder injected by Android Studio 2026.1+ — must be ignored.
        val dirs = resolvePluginDirs(
            userHome = root.path,
            osName = "Linux",
            properties = mapOf(
                "idea.config.path" to "some/non/existent/path",
                "idea.vendor.name" to "JetBrains",
                "idea.version" to "2026.1.3",
            ),
            env = emptyMap(),
        )
        assertTrue(
            dirs.none { it.path.contains("non/existent") },
            "Relative config path must be ignored, but dirs=$dirs",
        )
    }

    @Test fun `resolvePluginDirs returns config plugins dir when config path is absolute`(@TempDir root: File) {
        val configDir = File(root, "config/JetBrains/IntelliJIdea2026.1").also { it.mkdirs() }
        val configPluginsDir = File(configDir, "plugins").also { it.mkdirs() }
        val dirs = resolvePluginDirs(
            userHome = root.path,
            osName = "Linux",
            properties = mapOf("idea.config.path" to configDir.path),
            env = emptyMap(),
        )
        assertTrue(dirs.contains(configPluginsDir), "Expected $configPluginsDir in $dirs")
    }

    @Test fun `resolvePluginDirs adds XDG data dir when config path is under xdg config on Linux`(@TempDir root: File) {
        // If idea.config.path is ~/.config/JetBrains/IntelliJIdea2026.1,
        // the user-data (plugin) dir is ~/.local/share/JetBrains/IntelliJIdea2026.1.
        val configDir = File(root, ".config/JetBrains/IntelliJIdea2026.1").also { it.mkdirs() }
        val xdgDataPluginsDir = File(root, ".local/share/JetBrains/IntelliJIdea2026.1").also { it.mkdirs() }
        val dirs = resolvePluginDirs(
            userHome = root.path,
            osName = "Linux",
            properties = mapOf("idea.config.path" to configDir.path),
            env = emptyMap(),
        )
        assertTrue(dirs.contains(xdgDataPluginsDir), "Expected XDG data dir $xdgDataPluginsDir in $dirs")
    }

    @Test fun `resolvePluginDirs does not add XDG data dir on macOS even if config path is absolute`(@TempDir root: File) {
        // XDG layout only applies on Linux.
        val configDir = File(root, ".config/JetBrains/IntelliJIdea2026.1").also { it.mkdirs() }
        val xdgDataDir = File(root, ".local/share/JetBrains/IntelliJIdea2026.1").also { it.mkdirs() }
        val dirs = resolvePluginDirs(
            userHome = root.path,
            osName = "Mac OS X",
            properties = mapOf("idea.config.path" to configDir.path),
            env = emptyMap(),
        )
        assertFalse(dirs.contains(xdgDataDir), "XDG data dir must not be added on macOS")
    }

    // =========================================================================
    // resolvePluginDirs — source 3: user data dir, Linux
    // =========================================================================
    // On Linux, user-installed plugins live directly in the selector dir (no /plugins/ subdir).

    @Test fun `resolvePluginDirs finds IntelliJIdea Ultimate user plugin dir on Linux`(@TempDir root: File) {
        // The bug this test guards: IdeaIC* prefix was used instead of IntelliJIdea*.
        val pluginDir = File(root, ".local/share/JetBrains/IntelliJIdea2026.1").also { it.mkdirs() }
        val dirs = resolvePluginDirs(
            userHome = root.path,
            osName = "Linux",
            properties = mapOf(
                "idea.vendor.name" to "JetBrains",
                "idea.version" to "2026.1.3",  // patch must be stripped to match dir name
            ),
            env = emptyMap(),
        )
        assertTrue(dirs.contains(pluginDir), "Expected $pluginDir in $dirs")
    }

    @Test fun `resolvePluginDirs finds IdeaIC Community user plugin dir on Linux`(@TempDir root: File) {
        val pluginDir = File(root, ".local/share/JetBrains/IdeaIC2026.1").also { it.mkdirs() }
        val dirs = resolvePluginDirs(
            userHome = root.path,
            osName = "Linux",
            properties = mapOf("idea.vendor.name" to "JetBrains", "idea.version" to "2026.1.3"),
            env = emptyMap(),
        )
        assertTrue(dirs.contains(pluginDir), "Expected $pluginDir in $dirs")
    }

    @Test fun `resolvePluginDirs finds both IdeaIC and IntelliJIdea dirs simultaneously on Linux`(@TempDir root: File) {
        // A developer could have both Community and Ultimate installed at the same time.
        val communityDir = File(root, ".local/share/JetBrains/IdeaIC2026.1").also { it.mkdirs() }
        val ultimateDir  = File(root, ".local/share/JetBrains/IntelliJIdea2026.1").also { it.mkdirs() }
        val dirs = resolvePluginDirs(
            userHome = root.path,
            osName = "Linux",
            properties = mapOf("idea.vendor.name" to "JetBrains", "idea.version" to "2026.1"),
            env = emptyMap(),
        )
        assertTrue(dirs.contains(communityDir), "Expected Community dir in $dirs")
        assertTrue(dirs.contains(ultimateDir), "Expected Ultimate dir in $dirs")
    }

    @Test fun `resolvePluginDirs does not match older version dir on Linux`(@TempDir root: File) {
        File(root, ".local/share/JetBrains/IntelliJIdea2025.3").mkdirs()
        val dirs = resolvePluginDirs(
            userHome = root.path,
            osName = "Linux",
            properties = mapOf("idea.vendor.name" to "JetBrains", "idea.version" to "2026.1.3"),
            env = emptyMap(),
        )
        assertTrue(
            dirs.none { it.name == "IntelliJIdea2025.3" },
            "Older version dir must not be included, but dirs=$dirs",
        )
    }

    @Test fun `resolvePluginDirs finds Android Studio user plugin dir on Linux`(@TempDir root: File) {
        val pluginDir = File(root, ".local/share/Google/AndroidStudio2026.1").also { it.mkdirs() }
        val dirs = resolvePluginDirs(
            userHome = root.path,
            osName = "Linux",
            properties = mapOf("idea.vendor.name" to "Google", "idea.version" to "2026.1"),
            env = emptyMap(),
        )
        assertTrue(dirs.contains(pluginDir), "Expected $pluginDir in $dirs")
    }

    @Test fun `resolvePluginDirs respects XDG_DATA_HOME for JetBrains on Linux`(@TempDir root: File) {
        val xdgData = File(root, "xdg-data").also { it.mkdirs() }
        val pluginDir = File(xdgData, "JetBrains/IntelliJIdea2026.1").also { it.mkdirs() }
        val dirs = resolvePluginDirs(
            userHome = root.path,
            osName = "Linux",
            properties = mapOf("idea.vendor.name" to "JetBrains", "idea.version" to "2026.1"),
            env = mapOf("XDG_DATA_HOME" to xdgData.path),
        )
        assertTrue(dirs.contains(pluginDir), "Expected $pluginDir in $dirs")
    }

    @Test fun `resolvePluginDirs returns empty from scan when idea version is not set`(@TempDir root: File) {
        // Without idea.version the family prefix cannot be built; sources 3-5 must be skipped.
        File(root, ".local/share/JetBrains/IntelliJIdea2026.1").mkdirs()
        val dirs = resolvePluginDirs(
            userHome = root.path,
            osName = "Linux",
            properties = mapOf("idea.vendor.name" to "JetBrains"),  // no idea.version
            env = emptyMap(),
        )
        assertTrue(dirs.isEmpty(), "Without idea.version no dirs should be found, but dirs=$dirs")
    }

    // =========================================================================
    // resolvePluginDirs — source 3: user data dir, macOS
    // =========================================================================
    // On macOS, user-installed plugins live in <selector>/plugins/ (extra subdir vs Linux).

    @Test fun `resolvePluginDirs finds IntelliJIdea Ultimate user plugin dir on macOS`(@TempDir root: File) {
        val pluginsDir = File(root, "Library/Application Support/JetBrains/IntelliJIdea2026.1/plugins")
            .also { it.mkdirs() }
        val dirs = resolvePluginDirs(
            userHome = root.path,
            osName = "Mac OS X",
            properties = mapOf("idea.vendor.name" to "JetBrains", "idea.version" to "2026.1.3"),
            env = emptyMap(),
        )
        assertTrue(dirs.contains(pluginsDir), "Expected $pluginsDir in $dirs")
    }

    @Test fun `resolvePluginDirs finds IdeaIC Community user plugin dir on macOS`(@TempDir root: File) {
        val pluginsDir = File(root, "Library/Application Support/JetBrains/IdeaIC2026.1/plugins")
            .also { it.mkdirs() }
        val dirs = resolvePluginDirs(
            userHome = root.path,
            osName = "Mac OS X",
            properties = mapOf("idea.vendor.name" to "JetBrains", "idea.version" to "2026.1.3"),
            env = emptyMap(),
        )
        assertTrue(dirs.contains(pluginsDir), "Expected $pluginsDir in $dirs")
    }

    @Test fun `resolvePluginDirs finds Android Studio user plugin dir on macOS`(@TempDir root: File) {
        val pluginsDir = File(root, "Library/Application Support/Google/AndroidStudio2026.1/plugins")
            .also { it.mkdirs() }
        val dirs = resolvePluginDirs(
            userHome = root.path,
            osName = "Mac OS X",
            properties = mapOf("idea.vendor.name" to "Google", "idea.version" to "2026.1"),
            env = emptyMap(),
        )
        assertTrue(dirs.contains(pluginsDir), "Expected $pluginsDir in $dirs")
    }

    @Test fun `resolvePluginDirs does not return bare selector dir on macOS`(@TempDir root: File) {
        // The selector dir itself (without /plugins/) must NOT appear — only selector/plugins/ does.
        val selectorDir = File(root, "Library/Application Support/JetBrains/IntelliJIdea2026.1")
            .also { it.mkdirs() }
        // Create plugins subdir so it exists; verify the selector dir itself is not returned.
        File(selectorDir, "plugins").mkdirs()
        val dirs = resolvePluginDirs(
            userHome = root.path,
            osName = "Mac OS X",
            properties = mapOf("idea.vendor.name" to "JetBrains", "idea.version" to "2026.1"),
            env = emptyMap(),
        )
        assertFalse(dirs.contains(selectorDir), "Bare selector dir must not appear on macOS, dirs=$dirs")
    }

    // =========================================================================
    // resolvePluginDirs — source 3: user data dir, Windows
    // =========================================================================
    // On Windows, user-installed plugins live in <selector>\plugins\ (same extra subdir as macOS).

    @Test fun `resolvePluginDirs finds IntelliJIdea Ultimate user plugin dir on Windows`(@TempDir root: File) {
        val appData = File(root, "AppData/Roaming").also { it.mkdirs() }
        val pluginsDir = File(appData, "JetBrains/IntelliJIdea2026.1/plugins").also { it.mkdirs() }
        val dirs = resolvePluginDirs(
            userHome = root.path,
            osName = "Windows 11",
            properties = mapOf("idea.vendor.name" to "JetBrains", "idea.version" to "2026.1.3"),
            env = mapOf("APPDATA" to appData.path),
        )
        assertTrue(dirs.contains(pluginsDir), "Expected $pluginsDir in $dirs")
    }

    @Test fun `resolvePluginDirs finds IdeaIC Community user plugin dir on Windows`(@TempDir root: File) {
        val appData = File(root, "AppData/Roaming").also { it.mkdirs() }
        val pluginsDir = File(appData, "JetBrains/IdeaIC2026.1/plugins").also { it.mkdirs() }
        val dirs = resolvePluginDirs(
            userHome = root.path,
            osName = "Windows 11",
            properties = mapOf("idea.vendor.name" to "JetBrains", "idea.version" to "2026.1.3"),
            env = mapOf("APPDATA" to appData.path),
        )
        assertTrue(dirs.contains(pluginsDir), "Expected $pluginsDir in $dirs")
    }

    @Test fun `resolvePluginDirs finds Android Studio user plugin dir on Windows`(@TempDir root: File) {
        val appData = File(root, "AppData/Roaming").also { it.mkdirs() }
        val pluginsDir = File(appData, "Google/AndroidStudio2026.1/plugins").also { it.mkdirs() }
        val dirs = resolvePluginDirs(
            userHome = root.path,
            osName = "Windows 11",
            properties = mapOf("idea.vendor.name" to "Google", "idea.version" to "2026.1"),
            env = mapOf("APPDATA" to appData.path),
        )
        assertTrue(dirs.contains(pluginsDir), "Expected $pluginsDir in $dirs")
    }

    @Test fun `resolvePluginDirs falls back to userHome AppData Roaming when APPDATA not set on Windows`(@TempDir root: File) {
        // When the APPDATA env var is absent the code defaults to <userHome>/AppData/Roaming.
        val pluginsDir = File(root, "AppData/Roaming/JetBrains/IntelliJIdea2026.1/plugins")
            .also { it.mkdirs() }
        val dirs = resolvePluginDirs(
            userHome = root.path,
            osName = "Windows 11",
            properties = mapOf("idea.vendor.name" to "JetBrains", "idea.version" to "2026.1"),
            env = emptyMap(),  // no APPDATA
        )
        assertTrue(dirs.contains(pluginsDir), "Expected $pluginsDir in $dirs")
    }

    // =========================================================================
    // resolvePluginDirs — source 4: JetBrains Toolbox, Linux
    // =========================================================================

    @Test fun `resolvePluginDirs finds Toolbox app matching IntelliJIdea dataDirectoryName on Linux`(@TempDir root: File) {
        val toolboxPlugins = File(root, ".local/share/JetBrains/Toolbox/apps/intellij-idea/plugins")
            .also { it.mkdirs() }
        File(toolboxPlugins.parentFile, "product-info.json").writeText(
            """{"dataDirectoryName": "IntelliJIdea2026.1", "version": "2026.1.3"}"""
        )
        val dirs = resolvePluginDirs(
            userHome = root.path,
            osName = "Linux",
            properties = mapOf("idea.vendor.name" to "JetBrains", "idea.version" to "2026.1.3"),
            env = emptyMap(),
        )
        assertTrue(dirs.contains(toolboxPlugins), "Expected $toolboxPlugins in $dirs")
    }

    @Test fun `resolvePluginDirs finds Toolbox app matching IdeaIC dataDirectoryName on Linux`(@TempDir root: File) {
        val toolboxPlugins = File(root, ".local/share/JetBrains/Toolbox/apps/intellij-community/plugins")
            .also { it.mkdirs() }
        File(toolboxPlugins.parentFile, "product-info.json").writeText(
            """{"dataDirectoryName": "IdeaIC2026.1", "version": "2026.1.3"}"""
        )
        val dirs = resolvePluginDirs(
            userHome = root.path,
            osName = "Linux",
            properties = mapOf("idea.vendor.name" to "JetBrains", "idea.version" to "2026.1.3"),
            env = emptyMap(),
        )
        assertTrue(dirs.contains(toolboxPlugins), "Expected $toolboxPlugins in $dirs")
    }

    @Test fun `resolvePluginDirs finds Toolbox Android Studio app on Linux`(@TempDir root: File) {
        val toolboxPlugins = File(root, ".local/share/JetBrains/Toolbox/apps/android-studio/plugins")
            .also { it.mkdirs() }
        // Compact JSON (no space after colon) — must also be recognised.
        File(toolboxPlugins.parentFile, "product-info.json").writeText(
            """{"dataDirectoryName":"AndroidStudio2026.1","version":"2026.1"}"""
        )
        val dirs = resolvePluginDirs(
            userHome = root.path,
            osName = "Linux",
            properties = mapOf("idea.vendor.name" to "Google", "idea.version" to "2026.1"),
            env = emptyMap(),
        )
        assertTrue(dirs.contains(toolboxPlugins), "Expected $toolboxPlugins in $dirs")
    }

    @Test fun `resolvePluginDirs recognises compact JSON format in product-info json for IntelliJIdea`(@TempDir root: File) {
        val toolboxPlugins = File(root, ".local/share/JetBrains/Toolbox/apps/intellij-idea/plugins")
            .also { it.mkdirs() }
        // No space after colon — the alternate spacing variant.
        File(toolboxPlugins.parentFile, "product-info.json").writeText(
            """{"dataDirectoryName":"IntelliJIdea2026.1","version":"2026.1.3"}"""
        )
        val dirs = resolvePluginDirs(
            userHome = root.path,
            osName = "Linux",
            properties = mapOf("idea.vendor.name" to "JetBrains", "idea.version" to "2026.1.3"),
            env = emptyMap(),
        )
        assertTrue(dirs.contains(toolboxPlugins), "Compact JSON must be recognised, dirs=$dirs")
    }

    @Test fun `resolvePluginDirs skips Toolbox app with non-matching dataDirectoryName`(@TempDir root: File) {
        val toolboxPlugins = File(root, ".local/share/JetBrains/Toolbox/apps/old-idea/plugins")
            .also { it.mkdirs() }
        File(toolboxPlugins.parentFile, "product-info.json").writeText(
            """{"dataDirectoryName": "IntelliJIdea2025.3", "version": "2025.3.7"}"""
        )
        val dirs = resolvePluginDirs(
            userHome = root.path,
            osName = "Linux",
            properties = mapOf("idea.vendor.name" to "JetBrains", "idea.version" to "2026.1.3"),
            env = emptyMap(),
        )
        assertFalse(dirs.contains(toolboxPlugins), "Old Toolbox app must not be included")
    }

    @Test fun `resolvePluginDirs skips Toolbox app when plugins dir does not exist`(@TempDir root: File) {
        // product-info.json matches but the plugins/ directory was never created.
        val appDir = File(root, ".local/share/JetBrains/Toolbox/apps/intellij-idea").also { it.mkdirs() }
        File(appDir, "product-info.json").writeText(
            """{"dataDirectoryName": "IntelliJIdea2026.1", "version": "2026.1.3"}"""
        )
        val dirs = resolvePluginDirs(
            userHome = root.path,
            osName = "Linux",
            properties = mapOf("idea.vendor.name" to "JetBrains", "idea.version" to "2026.1.3"),
            env = emptyMap(),
        )
        assertTrue(
            dirs.none { it == File(appDir, "plugins") },
            "Missing plugins/ dir must be filtered out, dirs=$dirs",
        )
    }

    // =========================================================================
    // resolvePluginDirs — source 4: JetBrains Toolbox, macOS
    // =========================================================================

    @Test fun `resolvePluginDirs finds Toolbox app on macOS`(@TempDir root: File) {
        val toolboxPlugins = File(
            root, "Library/Application Support/JetBrains/Toolbox/apps/intellij-idea/plugins",
        ).also { it.mkdirs() }
        File(toolboxPlugins.parentFile, "product-info.json").writeText(
            """{"dataDirectoryName": "IntelliJIdea2026.1", "version": "2026.1.3"}"""
        )
        val dirs = resolvePluginDirs(
            userHome = root.path,
            osName = "Mac OS X",
            properties = mapOf("idea.vendor.name" to "JetBrains", "idea.version" to "2026.1.3"),
            env = emptyMap(),
        )
        assertTrue(dirs.contains(toolboxPlugins), "Expected $toolboxPlugins in $dirs")
    }

    // =========================================================================
    // resolvePluginDirs — source 4: JetBrains Toolbox, Windows
    // =========================================================================

    @Test fun `resolvePluginDirs finds Toolbox app on Windows using LOCALAPPDATA`(@TempDir root: File) {
        val localAppData = File(root, "AppData/Local").also { it.mkdirs() }
        val toolboxPlugins = File(localAppData, "JetBrains/Toolbox/apps/intellij-idea/plugins")
            .also { it.mkdirs() }
        File(toolboxPlugins.parentFile, "product-info.json").writeText(
            """{"dataDirectoryName": "IntelliJIdea2026.1", "version": "2026.1.3"}"""
        )
        val dirs = resolvePluginDirs(
            userHome = root.path,
            osName = "Windows 11",
            properties = mapOf("idea.vendor.name" to "JetBrains", "idea.version" to "2026.1.3"),
            env = mapOf("LOCALAPPDATA" to localAppData.path),
        )
        assertTrue(dirs.contains(toolboxPlugins), "Expected $toolboxPlugins in $dirs")
    }

    @Test fun `resolvePluginDirs finds Toolbox app on Windows using userHome fallback when LOCALAPPDATA absent`(@TempDir root: File) {
        val toolboxPlugins = File(root, "AppData/Local/JetBrains/Toolbox/apps/intellij-idea/plugins")
            .also { it.mkdirs() }
        File(toolboxPlugins.parentFile, "product-info.json").writeText(
            """{"dataDirectoryName": "IntelliJIdea2026.1", "version": "2026.1.3"}"""
        )
        val dirs = resolvePluginDirs(
            userHome = root.path,
            osName = "Windows 11",
            properties = mapOf("idea.vendor.name" to "JetBrains", "idea.version" to "2026.1.3"),
            env = emptyMap(),  // no LOCALAPPDATA
        )
        assertTrue(dirs.contains(toolboxPlugins), "Expected $toolboxPlugins in $dirs")
    }

    // =========================================================================
    // resolvePluginDirs — source 5: IDE application bundle, macOS
    // =========================================================================
    // App bundles in ~/Applications/<Name>.app/Contents/plugins/ are scanned.
    // The /Applications/ system path is skipped in tests because it is a real system directory.

    @Test fun `resolvePluginDirs finds Android Studio app bundle in user Applications on macOS`(@TempDir root: File) {
        val bundlePlugins = File(root, "Applications/Android Studio.app/Contents/plugins")
            .also { it.mkdirs() }
        val dirs = resolvePluginDirs(
            userHome = root.path,
            osName = "Mac OS X",
            properties = mapOf("idea.vendor.name" to "Google", "idea.version" to "2026.1"),
            env = emptyMap(),
        )
        assertTrue(dirs.contains(bundlePlugins), "Expected macOS bundle plugins $bundlePlugins in $dirs")
    }

    @Test fun `resolvePluginDirs finds IntelliJ IDEA app bundle in user Applications on macOS`(@TempDir root: File) {
        val bundlePlugins = File(root, "Applications/IntelliJ IDEA.app/Contents/plugins")
            .also { it.mkdirs() }
        val dirs = resolvePluginDirs(
            userHome = root.path,
            osName = "Mac OS X",
            properties = mapOf("idea.vendor.name" to "JetBrains", "idea.version" to "2026.1"),
            env = emptyMap(),
        )
        assertTrue(dirs.contains(bundlePlugins), "Expected macOS bundle plugins $bundlePlugins in $dirs")
    }

    @Test fun `resolvePluginDirs finds versioned Android Studio app bundle on macOS`(@TempDir root: File) {
        // The .app name may include a version suffix; it still starts with "Android Studio".
        val bundlePlugins = File(root, "Applications/Android Studio 2026.1.app/Contents/plugins")
            .also { it.mkdirs() }
        val dirs = resolvePluginDirs(
            userHome = root.path,
            osName = "Mac OS X",
            properties = mapOf("idea.vendor.name" to "Google", "idea.version" to "2026.1"),
            env = emptyMap(),
        )
        assertTrue(dirs.contains(bundlePlugins), "Expected versioned bundle plugins $bundlePlugins in $dirs")
    }

    // =========================================================================
    // resolvePluginDirs — source 5: IDE application bundle, Windows
    // =========================================================================

    @Test fun `resolvePluginDirs finds Android Studio installation under PROGRAMFILES on Windows`(@TempDir root: File) {
        val programFiles = File(root, "Program Files").also { it.mkdirs() }
        val bundlePlugins = File(programFiles, "Google/Android Studio/plugins").also { it.mkdirs() }
        val dirs = resolvePluginDirs(
            userHome = root.path,
            osName = "Windows 11",
            properties = mapOf("idea.vendor.name" to "Google", "idea.version" to "2026.1"),
            env = mapOf("PROGRAMFILES" to programFiles.path),
        )
        assertTrue(dirs.contains(bundlePlugins), "Expected $bundlePlugins in $dirs")
    }

    @Test fun `resolvePluginDirs finds IntelliJ IDEA installation under PROGRAMFILES on Windows`(@TempDir root: File) {
        val programFiles = File(root, "Program Files").also { it.mkdirs() }
        val bundlePlugins = File(programFiles, "JetBrains/IntelliJ IDEA 2026.1/plugins")
            .also { it.mkdirs() }
        val dirs = resolvePluginDirs(
            userHome = root.path,
            osName = "Windows 11",
            properties = mapOf("idea.vendor.name" to "JetBrains", "idea.version" to "2026.1"),
            env = mapOf("PROGRAMFILES" to programFiles.path),
        )
        assertTrue(dirs.contains(bundlePlugins), "Expected $bundlePlugins in $dirs")
    }
}

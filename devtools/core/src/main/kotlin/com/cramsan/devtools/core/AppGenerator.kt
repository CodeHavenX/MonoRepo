package com.cramsan.devtools.core

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.appendText
import kotlin.io.path.exists
import kotlin.io.path.extension
import kotlin.io.path.isRegularFile
import kotlin.io.path.pathString
import kotlin.io.path.readText
import kotlin.io.path.writeText

private val TEMPLATE_EXTENSIONS = setOf("kt", "kts", "xml", "conf", "json", "yml", "html", "css")

/**
 * Scaffolds a complete new app by copying the `templatereplaceme` template, substituting
 * all placeholder strings in file contents and file names, and wiring the new app into
 * `settings.gradle.kts` and `build.gradle.kts`.
 *
 * @param appName lowercase app identifier, e.g. `"myapp"`
 * @param displayName PascalCase display name used in code, e.g. `"MyApp"`
 * @param includeWasm whether to include the `front-end/app-wasm` module
 * @param includeAndroid whether to include the `front-end/app-android` module
 * @param includeJvm whether to include the `front-end/app-jvm` module
 */
fun generateApp(
    repoRoot: Path,
    appName: String,
    displayName: String,
    includeWasm: Boolean = true,
    includeAndroid: Boolean = true,
    includeJvm: Boolean = true,
): GenerationResult {
    val dest = repoRoot.resolve(appName)
    require(!dest.exists()) { "Destination already exists: $dest" }

    copyTemplate(repoRoot, dest)
    substituteFileContents(dest, appName, displayName)
    renamePathComponents(dest, appName, displayName)
    removePlatformDirs(dest, includeWasm, includeAndroid, includeJvm)
    updateSettingsGradle(repoRoot, appName, includeWasm, includeAndroid, includeJvm)
    updateBuildGradle(repoRoot, appName, includeWasm, includeAndroid, includeJvm)

    return GenerationResult(
        createdFiles = listOf(dest.toString()),
        postGenerationChecklist =
        listOf(
            "[ ] Update Dockerfile image name in $appName/back-end/",
            "[ ] Configure docker-compose credentials for $appName",
            "[ ] Set Supabase/backend credentials in config files",
            "[ ] Add CI pipeline for $appName (e.g., .github/workflows/$appName.yml)",
            "[ ] Review and update DI modules in $appName/back-end/.../dependencyinjection/",
            "[ ] Review and update DI modules in $appName/front-end/shared-app/.../di/",
        ),
    )
}

private fun copyTemplate(repoRoot: Path, dest: Path) {
    val template = repoRoot.resolve("templatereplaceme")
    template.toFile().copyRecursively(dest.toFile())
    // Remove build artifacts from the copy
    Files.walk(dest).use { stream ->
        stream
            .filter { it.toFile().isDirectory && it.fileName.toString() == "build" }
            .sorted(Comparator.reverseOrder())
            .forEach { it.toFile().deleteRecursively() }
    }
}

private fun substituteFileContents(dest: Path, appName: String, displayName: String) {
    val appUpper = appName.uppercase().replace('-', '_')
    val appDash = appName.replace('_', '-')
    val appUnderscore = appName.replace('-', '_')

    Files.walk(dest).use { stream ->
        stream
            .filter { path ->
                path.isRegularFile() &&
                    path.extension in TEMPLATE_EXTENSIONS &&
                    !path.pathString.contains("/build/")
            }.forEach { file ->
                var content = file.readText()
                content = content.replace("TEMPLATEREPLACEME", appUpper)
                content = content.replace("template-replace-me", appDash)
                content = content.replace("template_replace_me", appUnderscore)
                content = content.replace("TemplateReplaceMe", displayName)
                content = content.replace("templatereplaceme", appName)
                file.writeText(content)
            }
    }
}

private fun renamePathComponents(dest: Path, appName: String, displayName: String) {
    val toRename = mutableListOf<Path>()
    Files.walk(dest).use { stream ->
        stream
            .filter { path ->
                val fileName = path.fileName.toString()
                "templatereplaceme" in fileName || "TemplateReplaceMe" in fileName
            }.forEach { toRename.add(it) }
    }

    // Sort deepest paths first so files are renamed before their parent directories
    toRename.sortByDescending { it.pathString.length }
    toRename.forEach { path ->
        if (!path.exists()) return@forEach
        val fileName = path.fileName.toString()
        val newName =
            fileName
                .replace("templatereplaceme", appName)
                .replace("TemplateReplaceMe", displayName)
        if (fileName != newName) {
            Files.move(path, path.parent.resolve(newName))
        }
    }
}

private fun removePlatformDirs(dest: Path, includeWasm: Boolean, includeAndroid: Boolean, includeJvm: Boolean) {
    if (!includeWasm) dest.resolve("front-end/app-wasm").toFile().deleteRecursively()
    if (!includeAndroid) dest.resolve("front-end/app-android").toFile().deleteRecursively()
    if (!includeJvm) dest.resolve("front-end/app-jvm").toFile().deleteRecursively()
}

private fun updateSettingsGradle(
    repoRoot: Path,
    appName: String,
    includeWasm: Boolean,
    includeAndroid: Boolean,
    includeJvm: Boolean,
) {
    val settings = repoRoot.resolve("settings.gradle.kts")
    val block =
        buildString {
            append("\ninclude(\"$appName:api\")\n")
            append("include(\"$appName:shared\")\n")
            append("include(\"$appName:back-end\")\n")
            append("include(\"$appName:front-end:shared-app\")\n")
            append("include(\"$appName:front-end:shared-ui\")\n")
            if (includeAndroid) append("include(\"$appName:front-end:app-android\")\n")
            if (includeJvm) append("include(\"$appName:front-end:app-jvm\")\n")
            if (includeWasm) append("include(\"$appName:front-end:app-wasm\")\n")
        }
    settings.appendText(block)
}

private fun updateBuildGradle(
    repoRoot: Path,
    appName: String,
    includeWasm: Boolean,
    includeAndroid: Boolean,
    includeJvm: Boolean,
) {
    val buildFile = repoRoot.resolve("build.gradle.kts")
    val dependsBlock =
        buildString {
            append("    dependsOn(\"$appName:api:release\")\n")
            append("    dependsOn(\"$appName:shared:release\")\n")
            append("    dependsOn(\"$appName:back-end:release\")\n")
            append("    dependsOn(\"$appName:front-end:shared-app:release\")\n")
            append("    dependsOn(\"$appName:front-end:shared-ui:release\")")
            if (includeAndroid) append("\n    dependsOn(\"$appName:front-end:app-android:release\")")
            if (includeJvm) append("\n    dependsOn(\"$appName:front-end:app-jvm:release\")")
            if (includeWasm) append("\n    dependsOn(\"$appName:front-end:app-wasm:release\")")
        }
    val marker = "    dependsOn(\"generateBuildArtifacts\")"
    val updated = buildFile.readText().replace(marker, "$dependsBlock\n$marker")
    buildFile.writeText(updated)
}

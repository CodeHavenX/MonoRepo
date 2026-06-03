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

private val CORE_MODULES = listOf("api", "shared", "back-end", "front-end:shared-app", "front-end:shared-ui")

/**
 * Scaffolds a complete new app by copying the `templatereplaceme` template, substituting
 * all placeholder strings in file contents and file names, and wiring the new app into
 * `settings.gradle.kts` and `build.gradle.kts`.
 *
 * @param appName lowercase app identifier, e.g. `"myapp"`
 * @param displayName PascalCase display name used in code, e.g. `"MyApp"`
 * @param initialComponent PascalCase name for the starter component, e.g. `"Sample"`.
 *   The template's `ComponentReplaceme`/`componentreplaceme` placeholders are replaced with this
 *   value so that new apps start with real (non-placeholder) class names.
 * @param includeWasm whether to include the `front-end/app-wasm` module
 * @param includeAndroid whether to include the `front-end/app-android` module
 * @param includeJvm whether to include the `front-end/app-jvm` module
 */
fun generateApp(
    repoRoot: Path,
    appName: String,
    displayName: String,
    initialComponent: String = "Sample",
    includeWasm: Boolean = true,
    includeAndroid: Boolean = true,
    includeJvm: Boolean = true,
): GenerationResult {
    val dest = repoRoot.resolve(appName)
    require(!dest.exists()) { "Destination already exists: $dest" }

    val subs = buildAppSubstitutions(appName, displayName, initialComponent)
    val allModules = CORE_MODULES + platformModules(includeWasm, includeAndroid, includeJvm)

    copyTemplate(repoRoot, dest)
    substituteFileContents(dest, subs)
    renamePathComponents(dest, subs)
    removePlatformDirs(dest, includeWasm, includeAndroid, includeJvm)
    updateSettingsGradle(repoRoot, appName, allModules)
    updateBuildGradle(repoRoot, appName, allModules)

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
    repoRoot.resolve("templatereplaceme").toFile().copyRecursively(dest.toFile())
    // Collect build artifact directories first, then delete to avoid mutating
    // the directory tree while the walk stream is still open.
    val buildDirs =
        Files.walk(dest).use { stream ->
            stream
                .filter { it.toFile().isDirectory && it.fileName.toString() == "build" }
                .toList()
        }
    buildDirs.forEach { it.toFile().deleteRecursively() }
}

private fun buildAppSubstitutions(
    appName: String,
    displayName: String,
    initialComponent: String,
): List<Pair<String, String>> {
    val appUpper = appName.uppercase().replace('-', '_')
    return listOf(
        "TEMPLATE_REPLACE_ME" to appUpper,
        "TEMPLATEREPLACEME" to appUpper,
        "template-replace-me" to appName.replace('_', '-'),
        "template_replace_me" to appName.replace('-', '_'),
        "TemplateReplaceMe" to displayName,
        "templatereplaceme" to appName,
        "ComponentReplaceme" to initialComponent,
        "componentreplaceme" to initialComponent.lowercase(),
        "FeatureReplaceme" to "Home",
        "featurereplaceme" to "home",
        "ActivityReplaceme" to "Main",
        "activityreplaceme" to "main",
    )
}

private fun substituteFileContents(dest: Path, subs: List<Pair<String, String>>) {
    Files.walk(dest).use { stream ->
        stream
            .filter { path ->
                path.isRegularFile() &&
                    (path.extension in TEMPLATE_EXTENSIONS || path.fileName.toString() == "Dockerfile") &&
                    !path.pathString.contains("/build/")
            }.forEach { file ->
                var content = file.readText()
                for ((from, to) in subs) {
                    content = content.replace(from, to)
                }
                file.writeText(content)
            }
    }
}

private fun renamePathComponents(dest: Path, subs: List<Pair<String, String>>) {
    val placeholders = subs.map { it.first }.toSet()

    val toRename = mutableListOf<Path>()
    Files.walk(dest).use { stream ->
        stream
            .filter { path -> placeholders.any { it in path.fileName.toString() } }
            .forEach { toRename.add(it) }
    }

    // Sort deepest paths first so files are renamed before their parent directories
    toRename.sortByDescending { it.pathString.length }
    toRename.forEach { path ->
        if (!path.exists()) return@forEach
        val fileName = path.fileName.toString()
        var newName = fileName
        for ((from, to) in subs) {
            newName = newName.replace(from, to)
        }
        if (fileName != newName) {
            val target = path.parent.resolve(newName)
            // If the target is an empty directory left over from a prior template cleanup,
            // remove it so Files.move() can succeed.
            if (target.toFile().isDirectory && target.toFile().list()?.isEmpty() == true) {
                target.toFile().delete()
            }
            Files.move(path, target)
        }
    }
}

private fun removePlatformDirs(dest: Path, includeWasm: Boolean, includeAndroid: Boolean, includeJvm: Boolean) {
    if (!includeWasm) dest.resolve("front-end/app-wasm").toFile().deleteRecursively()
    if (!includeAndroid) dest.resolve("front-end/app-android").toFile().deleteRecursively()
    if (!includeJvm) dest.resolve("front-end/app-jvm").toFile().deleteRecursively()
}

private fun platformModules(
    includeWasm: Boolean,
    includeAndroid: Boolean,
    includeJvm: Boolean,
): List<String> =
    buildList {
        if (includeAndroid) add("front-end:app-android")
        if (includeJvm) add("front-end:app-jvm")
        if (includeWasm) add("front-end:app-wasm")
    }

private fun updateSettingsGradle(
    repoRoot: Path,
    appName: String,
    allModules: List<String>,
) {
    val block =
        buildString {
            append("\n")
            allModules.forEach { append("include(\"$appName:$it\")\n") }
        }
    repoRoot.resolve("settings.gradle.kts").appendText(block)
}

private fun updateBuildGradle(
    repoRoot: Path,
    appName: String,
    allModules: List<String>,
) {
    val dependsBlock = allModules.joinToString("\n") { "    dependsOn(\"$appName:$it:release\")" }
    val marker = "    dependsOn(\"generateBuildArtifacts\")"
    val buildFile = repoRoot.resolve("build.gradle.kts")
    buildFile.writeText(buildFile.readText().replace(marker, "$dependsBlock\n$marker"))
}

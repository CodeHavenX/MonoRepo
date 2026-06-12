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

private val CORE_MODULES = listOf("api", "models", "back-end", "front-end:app", "front-end:ui-components")

internal const val TEMPLATE_DEFAULT_FEATURE = "Placeholder"

/**
 * Scaffolds a complete new app by copying the `templatereplaceme` template, substituting
 * all placeholder strings in file contents and file names, and wiring the new app into
 * `settings.gradle.kts` and `build.gradle.kts`.
 *
 * @param appName lowercase app identifier, e.g. `"myapp"`
 * @param displayName PascalCase display name used in code, e.g. `"MyApp"`
 * @param initialComponent PascalCase name for the starter component, e.g. `"Sample"`.
 *   The template's `ComponentReplaceMe`/`componentreplaceme` placeholders are replaced with this
 *   value so that new apps start with real (non-placeholder) class names.
 * @param includeWasm whether to include the `front-end/launcher-web` module
 * @param includeAndroid whether to include the `front-end/launcher-android` module
 * @param includeJvm whether to include the `front-end/launcher-desktop` module
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
    require(!appName.contains('-')) {
        "App name '$appName' contains a hyphen, which is invalid in Kotlin package names. " +
            "Use '${appName.replace("-", "")}' (no separator) or '${appName.replace('-', '_')}' (underscore) instead."
    }
    val dest = repoRoot.resolve(appName)
    require(!dest.exists()) { "Destination already exists: $dest" }

    val subs = buildAppSubstitutions(appName, displayName, initialComponent)
    val allModules = CORE_MODULES + platformModules(includeWasm, includeAndroid, includeJvm)

    copyTemplate(repoRoot, dest)
    // Contents must be substituted before paths are renamed — renaming first would produce
    // paths that no longer match the walk used for content substitution.
    substituteFileContents(dest, subs)
    renamePathComponents(dest, subs)
    // Delegate per-component file generation to the existing generators so that the
    // app skeleton only contains app-level boilerplate, not component-level templates.
    generateApi(repoRoot, initialComponent, appName)
    generateController(repoRoot, initialComponent, appName)
    generateService(repoRoot, initialComponent, appName)
    generateDatastore(repoRoot, initialComponent, appName, "Example")
    generateFrontendService(repoRoot, initialComponent, appName)
    generateManager(repoRoot, initialComponent, appName)
    removePlatformDirs(dest, includeWasm, includeAndroid, includeJvm)
    updateSettingsGradle(repoRoot, appName, allModules)
    updateBuildGradle(repoRoot, appName, allModules)

    return GenerationResult(
        createdFiles = listOf(dest.toString()),
        postGenerationChecklist =
        listOf(
            "[ ] Do a gradle sync in your IDE to load all the new modules",
            "",
            "# NOTE: The initial component '$initialComponent' already occupies the following",
            "# component slots. Using the same name with 'devtools create <type>' will fail",
            "# with a file-already-exists error. Choose a different name for new components:",
            "#   create controller  --name $initialComponent  (conflicts: ${initialComponent}Controller.kt)",
            "#   create service     --name $initialComponent  (conflicts: ${initialComponent}Service.kt)",
            "#   create datastore   --name $initialComponent  (conflicts: ${initialComponent}Datastore.kt)",
            "#   create frontend-service --name $initialComponent  (conflicts: ${initialComponent}Service.kt, ${initialComponent}ServiceImpl.kt)",
            "#   create manager     --name $initialComponent  (conflicts: ${initialComponent}Manager.kt)",
            "#   create api         --name $initialComponent  (conflicts: ${initialComponent}Api.kt, ${initialComponent}NetworkResponse.kt)",
        ),
    )
}

private fun copyTemplate(repoRoot: Path, dest: Path) {
    repoRoot.resolve("devtools/templates/app").toFile().copyRecursively(dest.toFile())
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
        "template-replace-me" to appName.replace('_', '-'),
        "TemplateReplaceMe" to displayName,
        "templatereplaceme" to appName,
        "ComponentReplaceMe" to initialComponent,
        "componentreplaceme" to initialComponent.lowercase(),
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
                file.writeText(applySubsToContent(file.readText(), subs))
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
    if (!includeWasm) dest.resolve("front-end/launcher-web").toFile().deleteRecursively()
    if (!includeAndroid) dest.resolve("front-end/launcher-android").toFile().deleteRecursively()
    if (!includeJvm) dest.resolve("front-end/launcher-desktop").toFile().deleteRecursively()
}

private fun platformModules(
    includeWasm: Boolean,
    includeAndroid: Boolean,
    includeJvm: Boolean,
): List<String> =
    buildList {
        if (includeAndroid) add("front-end:launcher-android")
        if (includeJvm) add("front-end:launcher-desktop")
        if (includeWasm) add("front-end:launcher-web")
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

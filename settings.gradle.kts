import java.io.File
import java.util.Properties

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

dependencyResolutionManagement {
    repositories {
        mavenLocal()
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

plugins {
    // See https://jmfayard.github.io/refreshVersions
    id("de.fayard.refreshVersions") version "0.60.5"
////                            # available:"0.60.6"
}

refreshVersions {
    rejectVersionIf {
        candidate.stabilityLevel != de.fayard.refreshVersions.core.StabilityLevel.Stable
    }
}

// "templates" excludes devtools/templates (scaffolding templates, not real modules) and
// "intellij-plugin" is a standalone build not wired into this root project.
val excludedDirNames = setOf(".git", ".gradle", ".kotlin", "build", "node_modules", "build-logic", "templates", "intellij-plugin")

fun discoverAllModules(rootDir: File): Map<String, File> {
    return rootDir.walkTopDown()
        .onEnter { it.name !in excludedDirNames }
        .filter { it.name == "build.gradle.kts" || it.name == "build.gradle" }
        .filter { it.parentFile != rootDir }
        .associate { buildFile ->
            val relativePath = buildFile.parentFile.relativeTo(rootDir).path
            val moduleName = ":" + relativePath.replace(File.separatorChar, ':')
            moduleName to buildFile.parentFile
        }
}

fun parseModuleDeps(buildFile: File): List<String> {
    if (!buildFile.exists()) return emptyList()

    val projectDepRegex = Regex("""project\(["']([^"']+)["']\)""")
    return buildFile.readLines()
        .flatMap { line -> projectDepRegex.findAll(line).map { it.groupValues[1] } }
        .distinct()
}

fun buildDependencyGraph(allModules: Map<String, File>): Map<String, List<String>> {
    return allModules.mapValues { (_, moduleDir) ->
        val buildFile = moduleDir.resolve("build.gradle.kts")
            .takeIf { it.exists() }
            ?: moduleDir.resolve("build.gradle")
        parseModuleDeps(buildFile)
    }
}

fun resolveTransitiveDeps(
    module: String,
    graph: Map<String, List<String>>,
    resolved: MutableSet<String> = linkedSetOf()
): Set<String> {
    if (module in resolved) return resolved
    if (!graph.containsKey(module)) {
        println("⚠️  Warning: '$module' not found in module graph — skipping")
        return resolved
    }
    resolved.add(module)
    graph[module].orEmpty().forEach { dep ->
        resolveTransitiveDeps(dep, graph, resolved)
    }
    return resolved
}

val activeModulesFile = file("active-modules.properties")

if (!activeModulesFile.exists()) {
    // ── Full sync (CI / fresh clone) ──────────
    discoverAllModules(rootDir).keys.sorted().forEach { include(it) }
} else {
    // ── Selective sync ────────────────────────
    val props = Properties().apply { load(activeModulesFile.inputStream()) }
    val requestedModules = props.getProperty("modules", "")
        .split(",")
        .map { it.trim() }
        .filter { it.isNotEmpty() }

    if (requestedModules.isEmpty()) {
        println("⚠️  active-modules.properties is empty — loading all modules")
        discoverAllModules(rootDir).keys.sorted().forEach { include(it) }
    } else {
        val allModules = discoverAllModules(rootDir)
        val graph = buildDependencyGraph(allModules)

        val toLoad = (requestedModules + ":detekt-rules")
            .flatMap { resolveTransitiveDeps(it, graph) }
            .toSortedSet()

        println(buildString {
            appendLine("🔧 Selective sync — resolving transitive deps...")
            appendLine("   Requested    : ${requestedModules.joinToString()}")
            appendLine("   Also loading : ${(toLoad - requestedModules.toSet()).joinToString().ifEmpty { "none" }}")
            appendLine("   Total modules: ${toLoad.size} / ${allModules.size}")
        })

        toLoad.forEach { include(it) }
    }
}

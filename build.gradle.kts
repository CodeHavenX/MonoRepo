plugins {
    id("com.android.application") apply false
    id("com.android.library") apply false
    kotlin("jvm") apply false
    kotlin("multiplatform") apply false
    id("com.google.devtools.ksp") apply false
    id("org.jetbrains.compose") apply false
    id("org.jetbrains.kotlin.plugin.compose") apply false
    kotlin("plugin.serialization") apply false
    id("androidx.navigation.safeargs.kotlin") apply false
    id("com.squareup.sqldelight") apply false
    id("com.gradleup.shadow") apply false
    id("com.github.gmazzo.buildconfig") apply false
    id("dev.detekt") apply false
    id("io.github.takahirom.roborazzi") apply false
    // Internal gradle plugins
    id("com.cramsan.detekt") apply false
    id("com.cramsan.dev-shortcuts")
    id("com.cramsan.release-task") apply false
    id("com.cramsan.supabase-task") apply false
    id("com.cramsan.kotlin-jvm-lib") apply false
    id("com.cramsan.kotlin-jvm-lib-compose") apply false
    id("com.cramsan.kotlin-jvm-application") apply false
    id("com.cramsan.kotlin-jvm-ktor") apply false
    id("com.cramsan.kotlin-mpp-common") apply false
    id("com.cramsan.kotlin-mpp-common-compose") apply false
    id("com.cramsan.kotlin-mpp-android-lib") apply false
    id("com.cramsan.kotlin-mpp-android-lib-compose") apply false
    id("com.cramsan.kotlin-mpp-android-app") apply false
    id("com.cramsan.kotlin-mpp-jvm") apply false
    id("com.cramsan.kotlin-mpp-jvm-compose") apply false
    id("com.cramsan.kotlin-mpp-ios") apply false
    id("com.cramsan.kotlin-mpp-js") apply false
    id("com.cramsan.kotlin-mpp-wasm") apply false
    id("com.cramsan.kotlin-mpp-wasm-compose-app") apply false
}

subprojects {
    apply(plugin = "com.cramsan.detekt")

    // Configure Java toolchain to use JDK 21 for all subprojects
    pluginManager.withPlugin("java") {
        extensions.configure<JavaPluginExtension> {
            toolchain {
                languageVersion.set(JavaLanguageVersion.of(21))
            }
        }
    }
}

subprojects {
    // Add the custom architecture rules to every module except the rules module itself
    // (to avoid a circular project dependency).
    if (path != ":detekt-rules") {
        dependencies {
            "detektPlugins"(project(":detekt-rules"))
        }
    }
}

/**
 * Collects CI artifact paths from all opted-in modules and writes build-artifacts.txt.
 * Modules opt in by setting `val ciDeployable by extra(true)` in their build.gradle.kts.
 */
tasks.register("generateBuildArtifacts") {
    group = "ci"
    description = "Aggregates CI artifact paths from all deployable modules into build-artifacts.txt"
    dependsOn(subprojects.map { it.tasks.matching { t -> t.name == "writeCIArtifactPath" } })
    // Resolve to plain File objects at configuration time so the configuration cache
    // does not need to serialize Project references.
    val stagingFiles: List<File> = subprojects.map { sub ->
        sub.layout.buildDirectory.file("ci-artifact-path.txt").get().asFile
    }
    val outputFile: File = layout.projectDirectory.file("build-artifacts.txt").asFile
    inputs.files(stagingFiles)
    outputs.file(outputFile)
    doLast {
        val paths = stagingFiles
            .filter { it.exists() }
            .map { it.readText().trim() }
            .sorted()
        outputFile.writeText(paths.joinToString("\n") + "\n")
    }
}

/**
 * Production task settings for all projects. These must pass to consider
 * all projects are running correctly.
 */
val releaseAll = tasks.register("releaseAll") {
    group = "dev shortcuts"
    description = "Builds all target"

    dependsOn("generateBuildArtifacts")
    val repoDir: File = rootDir
    doLast {
        val process = ProcessBuilder("git", "status", "--porcelain")
            .directory(repoDir)
            .redirectErrorStream(true)
            .start()
        val modifiedFiles = process.inputStream.bufferedReader().readText().trim()
        process.waitFor()
        if (modifiedFiles.isNotEmpty()) {
            throw GradleException(
                "Uncommitted changes detected after releaseAll. " +
                    "Detekt may have auto-corrected formatting issues. " +
                    "Review and commit the following files:\n\n" +
                    modifiedFiles.lines().joinToString("\n") { "  $it" }
            )
        }
    }
}

// Wired up after all (selectively-synced) subprojects are configured, so releaseAll only
// depends on the "release" task of modules that are actually included in this build.
gradle.projectsEvaluated {
    releaseAll.configure {
        dependsOn(subprojects.mapNotNull { it.tasks.findByName("release") })
    }
}

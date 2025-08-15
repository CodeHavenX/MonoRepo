@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
}

val wasmModuleName by extra("SamplesWasmApp")

apply(from = "$rootDir/gradle/kotlin-mpp-target-common-compose.gradle")
apply(from = "$rootDir/gradle/kotlin-mpp-target-wasm-compose-application.gradle")

// https://github.com/CodeHavenX/MonoRepo/issues/228
// Remove this workaround once we upgrade packages.
// When upgrading kotlin, also update this line manually.
configurations.all {
    resolutionStrategy {
        force("org.jetbrains.kotlin:kotlin-stdlib-wasm-js:2.2.0")
    }
}

kotlin {
    wasmJs {
        // Refactored from KotlinWebpackConfig to avoid serialization issues
        // https://youtrack.jetbrains.com/issue/KT-68614/Wasm.-KotlinWebpack-cannot-serialize-Gradle-script-object-references
        val projectDirPath = project.projectDir.path

        outputModuleName = wasmModuleName
        browser {
            commonWebpackConfig {
                outputFileName = "composeApp.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static = (static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        add(projectDirPath)
                    }
                }
            }
        }
        binaries.executable()
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":samples:jbcompose-mpp-lib"))
            }
        }
    }
}
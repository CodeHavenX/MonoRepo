@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
}

apply(from = "$rootDir/gradle/kotlin-mpp-target-common-compose.gradle")
apply(from = "$rootDir/gradle/kotlin-mpp-target-wasm-compose-application.gradle")

kotlin {
    wasmJs {
        // Refactored from KotlinWebpackConfig to avoid serialization issues
        // https://youtrack.jetbrains.com/issue/KT-68614/Wasm.-KotlinWebpack-cannot-serialize-Gradle-script-object-references
        val projectDirPath = project.projectDir.path

        outputModuleName = "composeApp"
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
                implementation(project(":framework-samples:framework-sample-app"))

                implementation(project(":framework:interfacelib"))

                implementation("io.insert-koin:koin-core:_")
                implementation("io.insert-koin:koin-compose:_")
            }
        }
    }
}

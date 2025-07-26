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
        val projectDirPath = project.projectDir.path

        outputModuleName = "taskManagementApp"
        browser {
            commonWebpackConfig {
                outputFileName = "taskManagementApp.js"
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
                implementation(project(":samples:frontend-architecture-sample:shared"))
                implementation(project(":samples:frontend-architecture-sample:front-end:shared-ui"))
                implementation(project(":samples:frontend-architecture-sample:front-end:shared-app"))
            }
        }
    }
}
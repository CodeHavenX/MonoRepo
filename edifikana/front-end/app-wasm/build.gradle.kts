@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
    id("com.cramsan.kotlin-mpp-common-compose")
    id("com.cramsan.kotlin-mpp-wasm-compose-app")
}

val wasmModuleName by extra("EdifikanaWasmApp")

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
                    static(projectDirPath)
                }
            }
        }
        binaries.executable()
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":edifikana:front-end:shared-app"))

                implementation(project(":framework:interfacelib"))
                implementation(project(":framework:core-compose"))

                implementation("io.insert-koin:koin-core:_")
                implementation("io.insert-koin:koin-compose:_")
                implementation("io.insert-koin:koin-compose-viewmodel:_")
            }
        }
    }
}

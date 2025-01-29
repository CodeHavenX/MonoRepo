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
        moduleName = "composeApp"
        browser {
            commonWebpackConfig {
                outputFileName = "composeApp.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static = (static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        add(project.projectDir.path)
                    }
                }
            }
        }
        binaries.executable()
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":alpaca-scheduler:front-end:appcore"))

                implementation("io.insert-koin:koin-core:_")
                implementation("io.insert-koin:koin-compose:_")
            }
        }
    }
}

// Workaround for resources not being shared from dependencies.
// https://kotlinlang.slack.com/archives/C01F2HV7868/p1712948201704499
// https://github.com/Kotlin/kotlin-wasm-examples/blob/main/compose-imageviewer/webApp/build.gradle.kts
// TODO: Verify if the program runs without this workaround. If it does, remove this workaround.
val copyWasmResources = tasks.register("copyWasmResourcesWorkaround", Copy::class.java) {
    from(project(":alpaca-scheduler:front-end:appcore").file("src/commonMain/composeResources"))
    into("build/processedResources/wasmJs/main")
}
afterEvaluate {
    project.tasks.getByName("wasmJsProcessResources").finalizedBy(copyWasmResources)
    project.tasks.getByName("wasmJsDevelopmentExecutableCompileSync").dependsOn(copyWasmResources)
    project.tasks.getByName("wasmJsProductionExecutableCompileSync").dependsOn(copyWasmResources)
}
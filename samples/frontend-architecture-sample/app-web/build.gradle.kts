@file:Suppress("OPT_IN_IS_NOT_ENABLED")
@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
}

apply(from = "$rootDir/gradle/kotlin-mpp-target-wasm-compose-application.gradle")

kotlin {
    wasmJs {
        moduleName = "notesapp"
        browser {
            commonWebpackConfig {
                outputFileName = "notesapp.js"
            }
        }
        binaries.executable()
    }

    sourceSets {
        wasmJsMain.dependencies {
            implementation(project(":samples:frontend-architecture-sample:shared-lib"))
        }
    }
}

tasks.register("release") {
    group = "release"
    dependsOn("build")
}
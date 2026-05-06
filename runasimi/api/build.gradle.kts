@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("com.cramsan.kotlin-mpp-common")
    id("com.cramsan.kotlin-mpp-android-lib")
    id("com.cramsan.kotlin-mpp-jvm")
    id("com.cramsan.kotlin-mpp-wasm")
}

kotlin {
    androidLibrary {
        namespace = "com.cramsan.runasimi.api"
    }

    wasmJs {
        browser {}
        binaries.executable()
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":framework:interfacelib"))
                implementation(project(":framework:network-api"))
                implementation(project(":framework:annotations"))

                implementation("io.ktor:ktor-http:_")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:_")
            }
        }
    }
}


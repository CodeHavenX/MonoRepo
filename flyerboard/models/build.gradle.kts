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
    android {
        namespace = "com.cramsan.flyerboard.lib"
    }
}

kotlin {
    wasmJs {
        browser {}
        binaries.executable()
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":framework:annotations"))
                implementation(project(":framework:interfacelib"))

                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:_")
                implementation("io.ktor:ktor-openapi-schema:_")
            }
        }
    }
}

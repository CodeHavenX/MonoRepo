@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    kotlin("plugin.serialization")
}

apply(from = "$rootDir/gradle/kotlin-mpp-target-common.gradle")
apply(from = "$rootDir/gradle/kotlin-mpp-target-android-lib.gradle")
apply(from = "$rootDir/gradle/kotlin-mpp-target-jvm.gradle")
apply(from = "$rootDir/gradle/kotlin-mpp-target-wasm.gradle")

android {
    namespace = "com.cramsan.framework.httpserializers"
}

kotlin {
    wasmJs {
        browser {}
        binaries.executable()
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:_")
            }
        }
    }
}
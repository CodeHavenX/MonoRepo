@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    kotlin("plugin.serialization")
}

apply(from = "$rootDir/gradle/kotlin-mpp-target-common.gradle.kts")
apply(from = "$rootDir/gradle/kotlin-mpp-target-android-lib.gradle.kts")
apply(from = "$rootDir/gradle/kotlin-mpp-target-jvm.gradle.kts")
apply(from = "$rootDir/gradle/kotlin-mpp-target-wasm.gradle.kts")

android {
    namespace = "com.cramsan.edifikana.lib"
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

                implementation("org.jetbrains.kotlinx:kotlinx-datetime:_")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:_")
            }
        }
    }
}
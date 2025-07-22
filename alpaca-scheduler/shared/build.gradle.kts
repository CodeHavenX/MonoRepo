@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform")
    // Temporarily commenting out Android plugin due to firewall restrictions
    // id("com.android.library")
    kotlin("plugin.serialization")
}

apply(from = "$rootDir/gradle/kotlin-mpp-target-common.gradle")
// Temporarily commenting out Android target due to firewall restrictions
// apply(from = "$rootDir/gradle/kotlin-mpp-target-android-lib.gradle")
apply(from = "$rootDir/gradle/kotlin-mpp-target-ios.gradle")
apply(from = "$rootDir/gradle/kotlin-mpp-target-jvm.gradle")
apply(from = "$rootDir/gradle/kotlin-mpp-target-wasm.gradle")

// Temporarily commenting out Android configuration
// android {
//     namespace = "com.codehavenx.alpaca.shared"
// }

kotlin {
    wasmJs {
        browser {}
        binaries.executable()
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:_")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:_")

                implementation("io.ktor:ktor-serialization-kotlinx-json:_")
            }
        }
    }
}
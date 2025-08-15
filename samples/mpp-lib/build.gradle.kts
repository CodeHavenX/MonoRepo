@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

apply(from = "$rootDir/gradle/kotlin-mpp-target-common.gradle")
apply(from = "$rootDir/gradle/kotlin-mpp-target-android-lib.gradle")
apply(from = "$rootDir/gradle/kotlin-mpp-target-ios.gradle")
apply(from = "$rootDir/gradle/kotlin-mpp-target-js.gradle")
apply(from = "$rootDir/gradle/kotlin-mpp-target-jvm.gradle")
apply(from = "$rootDir/gradle/kotlin-mpp-target-wasm.gradle")

android {
    namespace = "com.cramsan.samples.mpp_lib"
}

kotlin {
    wasmJs {
        browser()
    }
    js {
        nodejs()
    }
}
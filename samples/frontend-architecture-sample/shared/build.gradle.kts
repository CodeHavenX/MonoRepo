@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

version = "1.0-SNAPSHOT"

apply(from = "$rootDir/gradle/kotlin-mpp-target-common.gradle")
apply(from = "$rootDir/gradle/kotlin-mpp-target-jvm.gradle")
apply(from = "$rootDir/gradle/kotlin-mpp-target-wasm.gradle")

kotlin {
    wasmJs {
        browser {}
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:_")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:_")
            implementation("org.jetbrains.kotlinx:kotlinx-datetime:_")
        }
        
        commonTest.dependencies {
            implementation("org.jetbrains.kotlin:kotlin-test:_")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:_")
        }
    }
}
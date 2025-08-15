@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    id("com.android.library")
    kotlin("multiplatform")
}

apply(from = "$rootDir/gradle/kotlin-mpp-target-common.gradle.kts")
apply(from = "$rootDir/gradle/kotlin-mpp-target-android-lib.gradle.kts")
apply(from = "$rootDir/gradle/kotlin-mpp-target-ios.gradle.kts")
apply(from = "$rootDir/gradle/kotlin-mpp-target-js.gradle.kts")
apply(from = "$rootDir/gradle/kotlin-mpp-target-jvm.gradle.kts")
apply(from = "$rootDir/gradle/kotlin-mpp-target-wasm.gradle.kts")

android {
    namespace = "com.cramsan.framework.assertlib"
}

kotlin {
    wasmJs {
        browser()
    }
    js {
        nodejs()
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":framework:halt"))
                implementation(project(":framework:interfacelib"))
                implementation(project(":framework:logging"))
            }
        }
        commonTest {
            dependencies {
                implementation(project(":framework:test"))
            }
        }
    }
}
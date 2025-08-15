@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

apply(from = "$rootDir/gradle/kotlin-mpp-target-common.gradle.kts")
apply(from = "$rootDir/gradle/kotlin-mpp-target-android-lib.gradle.kts")
apply(from = "$rootDir/gradle/kotlin-mpp-target-ios.gradle.kts")
apply(from = "$rootDir/gradle/kotlin-mpp-target-js.gradle.kts")
apply(from = "$rootDir/gradle/kotlin-mpp-target-jvm.gradle.kts")
apply(from = "$rootDir/gradle/kotlin-mpp-target-wasm.gradle.kts")

android {
    namespace = "com.cramsan.framework.halt"
}

dependencies {
    implementation("androidx.appcompat:appcompat:_")
    implementation("androidx.core:core-ktx:_")
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
                implementation(project(":framework:interfacelib"))
                implementation(project(":framework:logging"))
            }
        }
        commonTest {
            dependencies {
                implementation(project(":framework:test"))
            }
        }
        jvmTest {
            dependencies {
                implementation(project(":framework:test"))
            }
        }
    }
}
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
    namespace = "com.cramsan.framework.logging"
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
            }
        }
        commonTest {
            dependencies {
                implementation(project(":framework:test"))
            }
        }
        jvmMain {
            dependencies {
                implementation("org.apache.logging.log4j:log4j-api:_")
                implementation("org.apache.logging.log4j:log4j-core:_")
                implementation("org.apache.logging.log4j:log4j-slf4j2-impl:_")
            }
        }
    }
}
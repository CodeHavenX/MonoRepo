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
    namespace = "com.cramsan.framework.interfacelib.test"
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
                implementation(project(":framework:test"))

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:_")
                implementation("org.jetbrains.kotlin:kotlin-test-common:_")
                implementation("org.jetbrains.kotlin:kotlin-test-annotations-common:_")
            }
        }
        jvmMain {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:_")
                implementation("org.junit.jupiter:junit-jupiter-api:_")
                implementation("org.jetbrains.kotlin:kotlin-test-junit5:_")
                implementation("io.mockk:mockk:_")
            }
        }
        jvmTest {
        }
        androidMain {
            dependencies {
                implementation("androidx.test:core:_")
                implementation("androidx.test.ext:junit:_")
                implementation("androidx.test.ext:junit-ktx:_")
                implementation("androidx.arch.core:core-common:_")
                implementation("androidx.arch.core:core-runtime:_")
                implementation("androidx.arch.core:core-testing:_")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:_")
                implementation("junit:junit:_")
                implementation("org.jetbrains.kotlin:kotlin-test:_")
                implementation("org.jetbrains.kotlin:kotlin-test-junit:_")
                implementation("io.mockk:mockk:_")
                implementation("io.mockk:mockk-android:_")
            }
        }
        jsMain {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-test-js:_")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:_")
            }
        }
        wasmJsMain {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-test-wasm-js:_")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test-wasm-js:_")
            }
        }
    }
}
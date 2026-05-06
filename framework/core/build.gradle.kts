@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform")
    id("com.cramsan.kotlin-mpp-common")
    id("com.cramsan.kotlin-mpp-android-lib")
    id("com.cramsan.kotlin-mpp-ios")
    id("com.cramsan.kotlin-mpp-js")
    id("com.cramsan.kotlin-mpp-jvm")
    id("com.cramsan.kotlin-mpp-wasm")
}

kotlin {
    androidLibrary {
        namespace = "com.cramsan.framework.core"
    }

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
                implementation(project(":framework:assert"))
            }
        }
        commonTest {
            dependencies {
                implementation(project(":framework:test"))
            }
        }

        androidMain {
            dependencies {
                implementation(project(":framework:interfacelib"))

                implementation("androidx.appcompat:appcompat:_")
                implementation("androidx.fragment:fragment-ktx:_")
                implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:_")
            }
        }
    }
}

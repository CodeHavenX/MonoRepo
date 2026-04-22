@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    id("com.android.library")
    kotlin("multiplatform")
    id("com.cramsan.kotlin-mpp-common")
    id("com.cramsan.kotlin-mpp-android-lib")
    id("com.cramsan.kotlin-mpp-ios")
    id("com.cramsan.kotlin-mpp-js")
    id("com.cramsan.kotlin-mpp-jvm")
    id("com.cramsan.kotlin-mpp-wasm")
}

android {
    namespace = "com.cramsan.framework.core"

    // TODO: Verify if we can remove this
    // https://github.com/CodeHavenX/MonoRepo/issues/186
    lint {
        disable += "NullSafeMutableLiveData"
    }
}

dependencies {
    implementation("com.google.dagger:hilt-android:_")

    implementation(project(":framework:interfacelib"))

    implementation("androidx.appcompat:appcompat:_")
    implementation("androidx.fragment:fragment-ktx:_")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:_")
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
                implementation(project(":framework:assert"))
            }
        }
        commonTest {
            dependencies {
                implementation(project(":framework:test"))
            }
        }
    }
}

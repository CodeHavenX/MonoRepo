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
    android {
        namespace = "com.cramsan.framework.userevents"
    }
}

dependencies {
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
    }
}


kotlin {
    sourceSets {
        getByName("androidMain").dependencies {
            implementation("com.microsoft.appcenter:appcenter-analytics:_")
            implementation("com.microsoft.appcenter:appcenter-crashes:_")
        }
    }
}
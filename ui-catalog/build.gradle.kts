@file:OptIn(ExperimentalWasmDsl::class, ExperimentalRoborazziApi::class)

import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp")
    id("io.github.takahirom.roborazzi")
    id("com.cramsan.kotlin-mpp-common-compose")
    id("com.cramsan.kotlin-mpp-android-lib-compose")
    id("com.cramsan.kotlin-mpp-ios")
    id("com.cramsan.kotlin-mpp-jvm-compose")
    id("com.cramsan.kotlin-mpp-wasm")
}

kotlin {
    wasmJs {
        browser {}
        binaries.executable()
    }

    sourceSets {
        commonMain{
            dependencies {
                implementation(project(":framework:core-compose"))

                implementation("org.jetbrains.kotlinx:kotlinx-datetime:_")
            }
        }

        getByName("androidHostTest") {
            dependencies {
                implementation(project(":framework:test-roborazzi"))
            }
        }
    }
}

kotlin {
    android {
        namespace = "com.cramsan.ui"

    }
}

roborazzi {
    generateComposePreviewRobolectricTests {
        enable = true
        packages = listOf("com.cramsan.ui")
    }
}

compose.resources {
    packageOfResClass = "ui_catalog"
    publicResClass = true
}

kotlin {
    sourceSets.getByName("androidMain") {
        resources.srcDir("src/commonMain/resources")
    }
}

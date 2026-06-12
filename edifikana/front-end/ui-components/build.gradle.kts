@file:OptIn(ExperimentalWasmDsl::class, ExperimentalRoborazziApi::class)

import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.android.library")
    id("com.google.devtools.ksp")
    id("io.github.takahirom.roborazzi")
    id("com.cramsan.kotlin-mpp-common-compose")
    id("com.cramsan.kotlin-mpp-android-lib-compose")
    id("com.cramsan.kotlin-mpp-jvm-compose")
    id("com.cramsan.kotlin-mpp-wasm")
}

kotlin {
    wasmJs {
        browser {}
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":framework:core"))
            implementation(project(":framework:core-compose"))
            implementation(project(":ui-catalog"))

            implementation("org.jetbrains.kotlinx:kotlinx-datetime:_")
            implementation("org.jetbrains.androidx.navigation:navigation-compose:_")

            implementation("io.coil-kt.coil3:coil:")
            implementation("io.coil-kt.coil3:coil-compose:_")
            implementation("io.coil-kt.coil3:coil-network-ktor3:_")

            implementation("io.github.jan-tennert.supabase:coil3-integration:_")
        }
        androidUnitTest {
            dependencies {
                implementation(project(":framework:test-roborazzi"))
            }
        }
    }
}

android {
    namespace = "com.cramsan.edifikana.client.ui"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")
}

compose.resources {
    packageOfResClass = "edifikana_ui"
}

roborazzi {
    generateComposePreviewRobolectricTests {
        enable = true
        packages = listOf("com.cramsan.edifikana.client.ui")
    }
}

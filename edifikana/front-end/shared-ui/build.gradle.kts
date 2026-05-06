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
    id("com.cramsan.kotlin-mpp-jvm-compose")
    id("com.cramsan.kotlin-mpp-wasm")
}

kotlin {
    androidLibrary {
        namespace = "com.cramsan.edifikana.client.ui"
    }

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
        val androidHostTest by getting {
            dependencies {
                implementation(project(":framework:test-roborazzi"))
            }
        }
    }
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


afterEvaluate {
    val destDir = layout.buildDirectory.dir("intermediates/assets/androidHostTest/mergeAndroidHostTestAssets")
    val uiCatalog = project(":ui-catalog")
    tasks.named("copyComposeResToHostTestAssets", Copy::class).configure {
        into(destDir)
        from(uiCatalog.layout.buildDirectory.dir(
            "generated/compose/resourceGenerator/assembledResources/jvmMain"
        ))
        dependsOn(uiCatalog.tasks.named("assembleJvmMainResources"))
    }
}

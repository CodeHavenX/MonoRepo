@file:Suppress("OPT_IN_IS_NOT_ENABLED")
@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl


plugins {
    kotlin("multiplatform")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
    id("com.cramsan.kotlin-mpp-common-compose")
    id("com.cramsan.kotlin-mpp-android-lib-compose")
    id("com.cramsan.kotlin-mpp-ios")
    id("com.cramsan.kotlin-mpp-jvm-compose")
    id("com.cramsan.kotlin-mpp-wasm")
}

version = "1.0-SNAPSHOT"

kotlin {
    androidLibrary {
        namespace = "com.cramsan.sample.mpplib.compose"
    }

    wasmJs {
        browser {}
        binaries.executable()
    }

    iosSimulatorArm64() {
        binaries.framework {
            baseName = "JBComposeMPPLib"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation("io.insert-koin:koin-core:_")
            implementation("io.insert-koin:koin-compose:_")

            implementation("org.jetbrains.androidx.navigation:navigation-compose:_")
            implementation("org.jetbrains.androidx.lifecycle:lifecycle-runtime-compose:_")
            implementation("org.jetbrains.androidx.lifecycle:lifecycle-viewmodel-compose:_")
        }
    }
}

compose.resources {
    packageOfResClass = "jbcompose_mpplib"
}

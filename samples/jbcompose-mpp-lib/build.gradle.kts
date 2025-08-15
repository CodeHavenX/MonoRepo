@file:Suppress("OPT_IN_IS_NOT_ENABLED")
@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl


plugins {
    kotlin("multiplatform")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
    id("com.android.library")
}

version = "1.0-SNAPSHOT"

apply(from = "$rootDir/gradle/kotlin-mpp-target-common-compose.gradle")
apply(from = "$rootDir/gradle/kotlin-mpp-target-android-lib-compose.gradle")
apply(from = "$rootDir/gradle/kotlin-mpp-target-ios.gradle.kts")
apply(from = "$rootDir/gradle/kotlin-mpp-target-jvm-compose.gradle.kts")
apply(from = "$rootDir/gradle/kotlin-mpp-target-wasm.gradle.kts")

android {
    namespace = "com.cramsan.sample.mpplib.compose"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")
}

kotlin {
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
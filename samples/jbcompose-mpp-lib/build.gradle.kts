@file:Suppress("OPT_IN_IS_NOT_ENABLED")

import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl


plugins {
    kotlin("multiplatform")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
    id("com.android.library")
}

version = "1.0-SNAPSHOT"

apply(from = "$rootDir/gradle/kotlin-mpp-target-common-compose.gradle")
apply(from = "$rootDir/gradle/kotlin-mpp-target-android-lib-compose.gradle")
apply(from = "$rootDir/gradle/kotlin-mpp-target-ios.gradle")
apply(from = "$rootDir/gradle/kotlin-mpp-target-jvm-compose.gradle")
apply(from = "$rootDir/gradle/kotlin-mpp-target-wasm.gradle")

android {
    namespace = "com.cramsan.sample.mpp_lib.compose"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")
}

kotlin {
    @OptIn(ExperimentalWasmDsl::class)
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
}

compose.resources {
    packageOfResClass = "jbcompose_mpplib"
}
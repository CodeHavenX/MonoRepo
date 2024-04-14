@file:Suppress("OPT_IN_IS_NOT_ENABLED")

plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

version = "1.0-SNAPSHOT"

apply(from = "$rootDir/gradle/kotlin-mpp-target-common-compose.gradle")
apply(from = "$rootDir/gradle/kotlin-mpp-target-android-compose.gradle")
apply(from = "$rootDir/gradle/kotlin-mpp-target-ios.gradle")
apply(from = "$rootDir/gradle/kotlin-mpp-target-jvm-compose.gradle")

android {
    namespace = "com.cramsan.sample.mpp_lib.compose"
}

kotlin {
    iosSimulatorArm64() {
        binaries.framework {
            baseName = "JBComposeMPPLib"
            isStatic = true
        }
    }
}
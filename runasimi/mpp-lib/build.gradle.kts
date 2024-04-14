@file:Suppress("OPT_IN_IS_NOT_ENABLED")

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.compose")
}

version = "1.0-SNAPSHOT"

apply(from = "$rootDir/gradle/kotlin-mpp-target-common-compose.gradle")
apply(from = "$rootDir/gradle/kotlin-mpp-target-android-compose.gradle")
apply(from = "$rootDir/gradle/kotlin-mpp-target-ios.gradle")

android {
    namespace = "com.cramsan.runasimi.mpplib"
}

kotlin {
    iosSimulatorArm64() {
        binaries.framework {
            baseName = "MPPLib"
            isStatic = true
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":framework:interfacelib"))
                implementation(project(":framework:logging"))

                implementation("io.ktor:ktor-client-core:_")
            }
        }

    }
}
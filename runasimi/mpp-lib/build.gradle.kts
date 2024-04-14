@file:Suppress("OPT_IN_IS_NOT_ENABLED")

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
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
    cocoapods {
        summary = "Some description for the JBCcomposeMPPLib Module"
        homepage = "Link to the jbcompose-ios-app Module homepage"
        ios.deploymentTarget = "14.1"
        podfile = project.file("../ios-app/Podfile")
        framework {
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
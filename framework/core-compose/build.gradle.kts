@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
    id("com.google.devtools.ksp")
}

apply(from = "$rootDir/gradle/kotlin-mpp-target-common-compose.gradle")
apply(from = "$rootDir/gradle/kotlin-mpp-target-android-lib-compose.gradle")
apply(from = "$rootDir/gradle/kotlin-mpp-target-jvm-compose.gradle.kts")
apply(from = "$rootDir/gradle/kotlin-mpp-target-ios.gradle.kts")
apply(from = "$rootDir/gradle/kotlin-mpp-target-wasm.gradle.kts")

android {
    namespace = "com.cramsan.framework.core.compose"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")
}

kotlin {
    wasmJs {
        browser()
    }
    sourceSets {
        commonMain.dependencies {
            implementation(project(":framework:interfacelib"))
            implementation(project(":framework:core"))

            implementation("org.jetbrains.androidx.navigation:navigation-compose:_")
            implementation("org.jetbrains.androidx.lifecycle:lifecycle-runtime-compose:_")
            implementation("org.jetbrains.androidx.lifecycle:lifecycle-viewmodel-compose:_")
        }

        commonTest.dependencies {
            implementation(project(":framework:test"))
        }
    }
}

dependencies {
    // To use Kotlin Symbol Processing (KSP)
    add("kspCommonMainMetadata", "androidx.room:room-compiler:_")
    add("kspAndroid", "androidx.room:room-compiler:_")
    add("kspJvm", "androidx.room:room-compiler:_")
}

compose.resources {
    packageOfResClass = "core_compose"
}

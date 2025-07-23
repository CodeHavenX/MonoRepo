@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
}

version = "1.0-SNAPSHOT"

apply(from = "$rootDir/gradle/kotlin-mpp-target-common-compose.gradle")
apply(from = "$rootDir/gradle/kotlin-mpp-target-jvm-compose.gradle")
apply(from = "$rootDir/gradle/kotlin-mpp-target-wasm.gradle")

kotlin {
    wasmJs {
        browser {}
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":samples:frontend-architecture-sample:shared"))
            
            implementation("io.insert-koin:koin-core:_")
            implementation("io.insert-koin:koin-compose:_")
            
            implementation("org.jetbrains.androidx.navigation:navigation-compose:_")
            implementation("org.jetbrains.androidx.lifecycle:lifecycle-runtime-compose:_")
            implementation("org.jetbrains.androidx.lifecycle:lifecycle-viewmodel-compose:_")
            
            implementation("org.jetbrains.kotlinx:kotlinx-datetime:_")
        }
    }
}

compose.resources {
    packageOfResClass = "frontend_architecture_shared_ui"
}
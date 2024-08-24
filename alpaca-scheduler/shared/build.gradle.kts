import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    kotlin("plugin.serialization")
}

apply(from = "$rootDir/gradle/kotlin-mpp-target-common.gradle")
apply(from = "$rootDir/gradle/kotlin-mpp-target-android-lib.gradle")
apply(from = "$rootDir/gradle/kotlin-mpp-target-ios.gradle")
apply(from = "$rootDir/gradle/kotlin-mpp-target-jvm.gradle")
apply(from = "$rootDir/gradle/kotlin-mpp-target-wasm.gradle")

android {
    namespace = "com.codehavenx.alpaca.shared"
}

kotlin {
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser {}
        binaries.executable()
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:_")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:_")

                implementation("io.ktor:ktor-serialization-kotlinx-json:_")
            }
        }
    }
}
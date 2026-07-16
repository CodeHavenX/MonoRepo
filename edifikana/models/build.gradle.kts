@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("com.cramsan.kotlin-mpp-common")
    id("com.cramsan.kotlin-mpp-android-lib")
    id("com.cramsan.kotlin-mpp-jvm")
    id("com.cramsan.kotlin-mpp-wasm")
}

kotlin {
    android {
        namespace = "com.cramsan.edifikana.lib"
    }
}

kotlin {
    wasmJs {
        browser {}
        binaries.executable()
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":framework:annotations"))
                implementation(project(":framework:interfacelib"))

                implementation("org.jetbrains.kotlinx:kotlinx-datetime:_")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:_")

                // Provides @JsonSchema.* annotations read by buildJsonSchema when generating
                // OpenAPI/Swagger docs on the back-end. This is a pure annotation/schema module
                // (no server runtime) and is published for all KMP targets.
                implementation("io.ktor:ktor-openapi-schema:_")
            }
        }
    }
}

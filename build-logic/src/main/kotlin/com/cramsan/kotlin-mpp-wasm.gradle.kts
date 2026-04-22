package com.cramsan

plugins {
    id("org.jetbrains.kotlin.multiplatform")
}

kotlin {
    wasmJs {
        browser { }
    }

    sourceSets {
        wasmJsTest.dependencies {
            implementation("org.jetbrains.kotlin:kotlin-test-wasm-js:_")
        }
    }
}

tasks.register("releaseWasm") {
    group = "release"
    description = "Run all the steps to build a releaseWasm artifact"
    dependsOn("wasmJsMainClasses")
    dependsOn("detektCommonMainSourceSet")
    dependsOn("detektWasmJsMainSourceSet")
}

tasks.named("release") {
    dependsOn("releaseWasm")
}

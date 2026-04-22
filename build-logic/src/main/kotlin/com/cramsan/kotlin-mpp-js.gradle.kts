package com.cramsan

plugins {
    id("org.jetbrains.kotlin.multiplatform")
}

kotlin {
    @Suppress("OPT_IN_USAGE")
    js(IR) { }

    sourceSets {
        jsMain.dependencies {
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:_")
        }
        jsTest.dependencies {
            implementation("org.jetbrains.kotlin:kotlin-test-js:_")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:_")
        }
    }
}

tasks.register("releaseJs") {
    group = "release"
    description = "Run all the steps to build a Js artifact"
    dependsOn("compileKotlinJs")
    dependsOn("detektCommonMainSourceSet")
    dependsOn("detektJsMainSourceSet")
    dependsOn("jsTest")
}

tasks.named("release") {
    dependsOn("releaseJs")
}

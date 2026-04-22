package com.cramsan

plugins {
    id("org.jetbrains.kotlin.multiplatform")
}

kotlin {
    iosSimulatorArm64()
}

tasks.register("releaseIos") {
    group = "release"
    description = "Run all the steps to build a releaseIos artifact"
    dependsOn("compileKotlinIosSimulatorArm64")
    dependsOn("detektCommonMainSourceSet")
    dependsOn("detektIosSimulatorArm64MainSourceSet")
    dependsOn("iosSimulatorArm64Test")
}

tasks.named("release") {
    dependsOn("releaseIos")
}

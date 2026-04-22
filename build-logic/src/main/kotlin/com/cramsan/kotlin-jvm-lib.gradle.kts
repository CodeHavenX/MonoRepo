package com.cramsan

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("com.cramsan.release-task")
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    "implementation"("org.jetbrains.kotlin:kotlin-stdlib-jdk8:_")
    "implementation"("org.jetbrains.kotlinx:kotlinx-coroutines-core:_")

    "testImplementation"("org.jetbrains.kotlinx:kotlinx-coroutines-test:_")
    "testImplementation"("org.junit.jupiter:junit-jupiter-api:_")
    "testImplementation"("org.junit.jupiter:junit-jupiter-params:_")
    "testImplementation"("org.jetbrains.kotlin:kotlin-test-junit5:_")
    "testImplementation"("io.mockk:mockk:_")

    "testRuntimeOnly"("org.junit.jupiter:junit-jupiter-engine:_")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

tasks.register("releaseJvm") {
    group = "release"
    description = "Run all the steps to build a release artifact"
    dependsOn("build")
    dependsOn("detektMain")
    dependsOn("test")
}

tasks.named("release") {
    dependsOn("releaseJvm")
}

package com.cramsan

plugins {
    id("org.jetbrains.kotlin.multiplatform")
}

kotlin {
    jvm {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        }
    }

    sourceSets {
        jvmMain.dependencies {
            implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:_")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:_")
        }
        jvmTest.dependencies {
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:_")
            implementation("org.junit.jupiter:junit-jupiter-api:_")
            implementation("org.junit.jupiter:junit-jupiter-params:_")
            implementation("org.jetbrains.kotlin:kotlin-test-junit5:_")
            implementation("io.mockk:mockk:_")
            runtimeOnly("org.junit.jupiter:junit-jupiter-engine:_")
        }
    }
}

tasks.named<Test>("jvmTest") {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

tasks.register("releaseJvm") {
    group = "release"
    description = "Run all the steps to build a releaseJvm artifact"
    dependsOn("compileKotlinJvm")
    dependsOn("detektCommonMainSourceSet")
    dependsOn("detektJvmMainSourceSet")
    dependsOn("jvmTest")
}

tasks.named("release") {
    dependsOn("releaseJvm")
}

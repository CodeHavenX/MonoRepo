package com.cramsan

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.cramsan.release-task")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }
    sourceSets {
        commonMain.dependencies {
            implementation("org.jetbrains.kotlin:kotlin-stdlib-common:_")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:_")
        }
        commonTest.dependencies {
            implementation("org.jetbrains.kotlin:kotlin-test-annotations-common:_")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:_")
            implementation("org.jetbrains.kotlin:kotlin-test-common:_")
            implementation("app.cash.turbine:turbine:_")
        }
    }
}

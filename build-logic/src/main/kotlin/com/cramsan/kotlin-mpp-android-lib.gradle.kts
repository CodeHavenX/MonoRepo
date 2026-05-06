package com.cramsan

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.kotlin.multiplatform.library")
}

kotlin {
    jvmToolchain(21)
    androidLibrary {
        compileSdk = project.property("compileSdkVersion").toString().toInt()
        minSdk = project.property("minSdkVersion").toString().toInt()
        withHostTestBuilder { }.configure {
            isIncludeAndroidResources = true
        }
    }
    sourceSets {
        androidMain.dependencies {
            implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:_")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:_")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:_")
        }
        // withHostTestBuilder creates the androidHostTest compilation and source set.
        // Point it at src/androidUnitTest/kotlin so existing test files are picked up
        // without renaming directories across all modules.
        val androidHostTest by getting {
            kotlin.srcDirs("src/androidHostTest/kotlin", "src/androidUnitTest/kotlin")
            dependencies {
                implementation("androidx.test:core:_")
                implementation("androidx.test.ext:junit:_")
                implementation("androidx.test.ext:junit-ktx:_")
                implementation("androidx.arch.core:core-common:_")
                implementation("androidx.arch.core:core-runtime:_")
                implementation("androidx.arch.core:core-testing:_")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:_")
                implementation("junit:junit:_")
                implementation("org.jetbrains.kotlin:kotlin-test:_")
                implementation("org.jetbrains.kotlin:kotlin-test-junit:_")
                implementation("io.mockk:mockk:_")
                implementation("io.mockk:mockk-android:_")
            }
        }
        androidInstrumentedTest.dependencies {
            implementation("androidx.test.ext:junit:_")
            implementation("androidx.test.ext:junit-ktx:_")
            implementation("androidx.test:core:_")
            implementation("androidx.test:rules:_")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:_")
            implementation("junit:junit:_")
            implementation("org.jetbrains.kotlin:kotlin-test:_")
            implementation("org.jetbrains.kotlin:kotlin-test-junit:_")
            implementation("io.mockk:mockk-android:_")
        }
    }
}

tasks.register("releaseAndroid") {
    group = "release"
    description = "Run all the steps to build a releaseAndroid artifact"
    dependsOn("assemble")
    dependsOn("testAndroidHostTest")
    dependsOn("detektCommonMainSourceSet")
    dependsOn("detektAndroidMainSourceSet")
    dependsOn("detektAndroidHostTestSourceSet")
}

tasks.named("release") {
    dependsOn("releaseAndroid")
}

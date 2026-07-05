package com.cramsan

plugins {
    id("com.android.kotlin.multiplatform.library")
    id("org.jetbrains.kotlin.multiplatform")
}

kotlin {
    android {
        compileSdk = project.property("compileSdkVersion").toString().toInt()
        minSdk = project.property("minSdkVersion").toString().toInt()

        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        }

        androidResources.enable = true

        withHostTestBuilder {}.configure {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }

        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }

    jvmToolchain(21)

    sourceSets {
        getByName("androidMain").dependencies {
            implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:_")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:_")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:_")
        }

        getByName("androidHostTest").dependencies {
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

        getByName("androidDeviceTest").dependencies {
            implementation("androidx.test.ext:junit:_")
            implementation("androidx.test.ext:junit-ktx:_")
            implementation("androidx.test:core:_")
            implementation("androidx.test:rules:_")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:_")
            implementation("junit:junit:_")
            implementation("org.jetbrains.kotlin:kotlin-test:_")
            implementation("org.jetbrains.kotlin:kotlin-test-junit:_")
            implementation("io.mockk:mockk-android:_")
            implementation("org.robolectric:robolectric:_")
        }
    }
}

tasks.withType<Test>().configureEach {
    systemProperty("robolectric.pixelCopyRenderMode", "hardware")
}

tasks.register("releaseAndroid") {
    group = "release"
    description = "Run all the steps to build a releaseAndroid artifact"
    dependsOn("assemble")
    dependsOn("detektCommonMainSourceSet")
    // detektAndroidMainSourceSet and detektAndroidHostTestSourceSet (type-resolution analysis of
    // the android compilations) crash with an internal detekt exception on Compose source files
    // ("findFirCompiledSymbol only works on compiled declarations") and also lint generated
    // Roborazzi test sources under build/. Re-enable once fixed upstream:
    // https://github.com/CodeHavenX/MonoRepo/issues/478
    // dependsOn("detektAndroidMainSourceSet")
    // dependsOn("detektAndroidHostTestSourceSet")
    dependsOn("testAndroidHostTest")
}

tasks.named("release") {
    dependsOn("releaseAndroid")
}

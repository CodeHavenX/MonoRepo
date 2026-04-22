package com.cramsan

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.compose")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("com.cramsan.release-task")
}

kotlin {
    androidTarget { }
    jvmToolchain(21)
}

android {
    compileSdk = project.property("compileSdkVersion").toString().toInt()

    defaultConfig {
        minSdk = project.property("minSdkVersion").toString().toInt()
        targetSdk = project.property("targetSdkVersion").toString().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }

    flavorDimensions += "stage"
    productFlavors {
        create("preprod") {
            dimension = "stage"
            applicationIdSuffix = ".preprod"
            versionNameSuffix = "-preprod"
        }
        create("prod") {
            dimension = "stage"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/**"
        }
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
            all { test ->
                test.testLogging {
                    events("passed", "skipped", "failed")
                }
            }
        }
    }

    lint {
        disable += "NullSafeMutableLiveData"
    }
}

dependencies {
    "kspAndroid"("com.google.dagger:hilt-compiler:_")
    "kspAndroid"("androidx.hilt:hilt-compiler:_")

    "implementation"("org.jetbrains.kotlin:kotlin-stdlib-jdk8:_")
    "implementation"("org.jetbrains.kotlinx:kotlinx-coroutines-core:_")
    "implementation"("org.jetbrains.kotlinx:kotlinx-coroutines-android:_")

    "implementation"("androidx.compose.ui:ui:_")
    "implementation"("androidx.compose.ui:ui-tooling-preview:_")
    "debugImplementation"("androidx.compose.ui:ui-tooling:_")
    "implementation"("androidx.compose.foundation:foundation:_")
    "implementation"("androidx.compose.material3:material3:_")
    "implementation"("androidx.activity:activity-compose:_")

    "implementation"("com.google.dagger:hilt-android:_")
    "implementation"("androidx.hilt:hilt-navigation-compose:_")

    "testImplementation"("androidx.test:core:_")
    "testImplementation"("androidx.test.ext:junit:_")
    "testImplementation"("androidx.test.ext:junit-ktx:_")
    "testImplementation"("androidx.arch.core:core-common:_")
    "testImplementation"("androidx.arch.core:core-runtime:_")
    "testImplementation"("androidx.arch.core:core-testing:_")
    "testImplementation"("org.jetbrains.kotlinx:kotlinx-coroutines-test:_")
    "testImplementation"("junit:junit:_")
    "testImplementation"("org.jetbrains.kotlin:kotlin-test:_")
    "testImplementation"("org.jetbrains.kotlin:kotlin-test-junit:_")
    "testImplementation"("io.mockk:mockk:_")
    "testImplementation"("io.mockk:mockk-android:_")

    "androidTestImplementation"("androidx.test.ext:junit:_")
    "androidTestImplementation"("androidx.test.ext:junit-ktx:_")
    "androidTestImplementation"("androidx.test:core:_")
    "androidTestImplementation"("androidx.test:rules:_")
    "androidTestImplementation"("org.jetbrains.kotlinx:kotlinx-coroutines-test:_")
    "androidTestImplementation"("junit:junit:_")
    "androidTestImplementation"("org.jetbrains.kotlin:kotlin-test:_")
    "androidTestImplementation"("org.jetbrains.kotlin:kotlin-test-junit:_")
    "androidTestImplementation"("io.mockk:mockk-android:_")
    "androidTestImplementation"("org.robolectric:robolectric:_")
}

tasks.register("releaseAndroid") {
    group = "release"
    description = "Run all the steps to build a release artifact"
    dependsOn("build")
    dependsOn("bundle")
    dependsOn("detektCommonMainSourceSet")
    dependsOn("detektAndroidDebugSourceSet")
}

tasks.named("release") {
    dependsOn("releaseAndroid")
}

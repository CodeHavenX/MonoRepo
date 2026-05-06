package com.cramsan

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp")
    id("com.cramsan.release-task")
}

kotlin {
    jvmToolchain(21)
}

android {
    compileSdk = project.property("compileSdkVersion").toString().toInt()

    sourceSets["main"].apply {
        kotlin.srcDirs("src/androidMain/kotlin")
        java.srcDirs("src/androidMain/java")
        res.srcDirs("src/androidMain/res")
        resources.srcDirs("src/androidMain/resources")
        manifest.srcFile("src/androidMain/AndroidManifest.xml")
    }
    sourceSets["test"].apply {
        kotlin.srcDirs("src/androidUnitTest/kotlin", "src/androidUnitTest/java")
    }
    sourceSets["androidTest"].apply {
        kotlin.srcDirs("src/androidInstrumentedTest/kotlin", "src/androidInstrumentedTest/java")
    }

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
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
                test.failOnNoDiscoveredTests = false
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
    "implementation"("org.jetbrains.kotlin:kotlin-stdlib-jdk8:_")
    "implementation"("org.jetbrains.kotlinx:kotlinx-coroutines-core:_")
    "implementation"("org.jetbrains.kotlinx:kotlinx-coroutines-android:_")

    "implementation"("androidx.compose.ui:ui:_")
    "implementation"("androidx.compose.ui:ui-tooling-preview:_")
    "debugImplementation"("androidx.compose.ui:ui-tooling:_")
    "implementation"("androidx.compose.foundation:foundation:_")
    "implementation"("androidx.compose.material3:material3:_")
    "implementation"("androidx.activity:activity-compose:_")

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
    dependsOn("assembleRelease")
    dependsOn("bundleRelease")
    dependsOn("testDebugUnitTest")
    dependsOn("detektMainSourceSet")
    dependsOn("detektTestSourceSet")
}

tasks.named("release") {
    dependsOn("releaseAndroid")
}

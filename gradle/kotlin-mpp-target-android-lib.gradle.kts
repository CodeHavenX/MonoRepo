/**
 * Plugin to apply the Android library target.
 */
apply(plugin = "com.android.library")
apply(plugin = "org.jetbrains.kotlin.multiplatform")

android {
    compileSdk = project.property("compileSdkVersion").toString().toInt()

    defaultConfig {
        minSdk = project.property("minSdkVersion").toString().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    packaging {
        resources {
            excludes += "/META-INF/**"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true

            all {
                // Enable printing the result of the unit tests.
                testLogging {
                    events("passed", "skipped", "failed")
                }
            }
        }
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:_")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:_")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:_")

    testImplementation("androidx.test:core:_")
    testImplementation("androidx.test.ext:junit:_")
    testImplementation("androidx.test.ext:junit-ktx:_")
    testImplementation("androidx.arch.core:core-common:_")
    testImplementation("androidx.arch.core:core-runtime:_")
    testImplementation("androidx.arch.core:core-testing:_")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:_")
    testImplementation("junit:junit:_")
    testImplementation("org.jetbrains.kotlin:kotlin-test:_")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:_")
    testImplementation("io.mockk:mockk:_")
    testImplementation("io.mockk:mockk-android:_")

    androidTestImplementation("androidx.test.ext:junit:_")
    androidTestImplementation("androidx.test.ext:junit-ktx:_")
    androidTestImplementation("androidx.test:core:_")
    androidTestImplementation("androidx.test:rules:_")
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:_")
    androidTestImplementation("junit:junit:_")
    androidTestImplementation("org.jetbrains.kotlin:kotlin-test:_")
    androidTestImplementation("org.jetbrains.kotlin:kotlin-test-junit:_")
    androidTestImplementation("io.mockk:mockk-android:_")
    androidTestImplementation("org.robolectric:robolectric:_")
}

kotlin {
    androidTarget()
}

tasks.register("releaseAndroid") {
    group = "release"
    description = "Run all the steps to build a releaseAndroid artifact"
    dependsOn("assembleDebug")
    dependsOn("assembleRelease")
    dependsOn("detektMetadataMain") // Run the code analyzer on the common-code source set
    dependsOn("detektAndroidDebug") // Run the code analyzer
    dependsOn("testDebugUnitTest")
    dependsOn("testReleaseUnitTest")
}

tasks.named("release").configure {
    dependsOn("releaseAndroid")
}
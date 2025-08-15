@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.android.library")
    id("com.google.devtools.ksp")
    id("io.github.takahirom.roborazzi")
}

apply(from = "$rootDir/gradle/kotlin-mpp-target-common-compose.gradle")
apply(from = "$rootDir/gradle/kotlin-mpp-target-android-lib-compose.gradle")
apply(from = "$rootDir/gradle/kotlin-mpp-target-jvm-compose.gradle.kts")
apply(from = "$rootDir/gradle/kotlin-mpp-target-wasm.gradle.kts")

kotlin {
    applyDefaultHierarchyTemplate()

    wasmJs {
        browser {}
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":framework:annotations"))
            implementation(project(":framework:assert"))
            implementation(project(":framework:halt"))
            implementation(project(":framework:interfacelib"))
            implementation(project(":framework:logging"))
            implementation(project(":framework:thread"))
            implementation(project(":framework:crashhandler"))
            implementation(project(":framework:core"))
            implementation(project(":framework:core-compose"))
            implementation(project(":framework:preferences"))
            implementation(project(":framework:utils"))
            implementation(project(":ui-catalog"))

            implementation("org.jetbrains.kotlinx:kotlinx-datetime:_")
            implementation("org.jetbrains.androidx.navigation:navigation-compose:_")
            implementation("org.jetbrains.androidx.lifecycle:lifecycle-runtime-compose:_")
            implementation("org.jetbrains.androidx.lifecycle:lifecycle-viewmodel-compose:_")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:_")

            implementation("io.insert-koin:koin-core:_")
            implementation("io.insert-koin:koin-compose:_")
            implementation("io.insert-koin:koin-compose-viewmodel:_")
        }

        jvmMain {
            dependencies {
                implementation("org.apache.logging.log4j:log4j-core:_")
                implementation("org.apache.logging.log4j:log4j-slf4j-impl:_")

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:_")
            }
        }

        androidMain {
            dependencies {
            }
        }
        androidUnitTest {
            dependencies {
                implementation(project(":framework:test-roborazzi"))
            }
        }

        wasmJsMain {
            dependencies {
            }
        }
    }
}

android {
    namespace = "com.cramsan.framework.sample.shared"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")
}

compose.resources {
    packageOfResClass = "framework_sample"
}

dependencies {
    // To use Kotlin Symbol Processing (KSP)
    add("kspCommonMainMetadata", "androidx.room:room-compiler:_")
    add("kspAndroid", "androidx.room:room-compiler:_")
    add("kspJvm", "androidx.room:room-compiler:_")

    implementation("androidx.appcompat:appcompat:_")
    implementation("androidx.core:core-ktx:_")
    implementation("androidx.compose.material:material-icons-extended:_")

    implementation("io.insert-koin:koin-core:_")
    implementation("io.insert-koin:koin-android:_")
    implementation("io.insert-koin:koin-androidx-compose:_")
}

roborazzi {
    generateComposePreviewRobolectricTests {
        enable = true
        packages = listOf("com.cramsan.framework.sample.shared")
    }
}
@file:OptIn(ExperimentalWasmDsl::class, ExperimentalRoborazziApi::class)

import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.android.library")
    id("androidx.room3")
    id("com.google.devtools.ksp")
    id("io.github.takahirom.roborazzi")
}

apply(from = "$rootDir/gradle/kotlin-mpp-target-common-compose.gradle")
apply(from = "$rootDir/gradle/kotlin-mpp-target-android-lib-compose.gradle")
apply(from = "$rootDir/gradle/kotlin-mpp-target-jvm-compose.gradle")
apply(from = "$rootDir/gradle/kotlin-mpp-target-wasm.gradle")

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
            implementation(project(":framework:configuration"))
            implementation(project(":framework:core-compose"))
            implementation(project(":framework:preferences"))
            implementation(project(":framework:test"))
            implementation(project(":framework:utils"))
            implementation(project(":framework:network-api"))
            implementation(project(":framework:http-serializers"))

            implementation(project(":architecture:front-end-architecture"))

            implementation(project(":ui-catalog"))

            implementation(project(":flyerboard:shared"))
            implementation(project(":flyerboard:api"))
            implementation(project(":flyerboard:front-end:shared-ui"))

            implementation("org.jetbrains.kotlinx:kotlinx-datetime:_")
            implementation("org.jetbrains.androidx.navigation:navigation-compose:_")
            implementation("org.jetbrains.androidx.lifecycle:lifecycle-runtime-compose:_")
            implementation("org.jetbrains.androidx.lifecycle:lifecycle-viewmodel-compose:_")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:_")

            implementation("io.insert-koin:koin-core:_")
            implementation("io.insert-koin:koin-compose:_")
            implementation("io.insert-koin:koin-compose-viewmodel:_")

            implementation("io.ktor:ktor-client-content-negotiation:_")
            implementation("io.ktor:ktor-serialization-kotlinx-json:_")

            implementation("io.github.jan-tennert.supabase:auth-kt:_")

            implementation("io.coil-kt.coil3:coil:")
            implementation("io.coil-kt.coil3:coil-compose:_")
            implementation("io.coil-kt.coil3:coil-network-ktor3:_")
        }

        val localDB by creating {
            dependsOn(commonMain.get())

            dependencies {
                implementation("androidx.room3:room3-runtime:_")
            }
        }

        jvmMain {
            dependsOn(localDB)

            dependencies {
                implementation("org.apache.logging.log4j:log4j-core:_")
                implementation("org.apache.logging.log4j:log4j-slf4j-impl:_")

                implementation("io.ktor:ktor-client-cio:_")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:_")

                // Room
                implementation("androidx.room3:room3-runtime:_")
                implementation("androidx.sqlite:sqlite-bundled-jvm:_")
            }
        }

        jvmTest {
            dependencies {
                implementation("io.ktor:ktor-client-mock:_")

                implementation(project(":framework:test"))
            }
        }

        androidMain {
            dependsOn(localDB)
        }
        androidUnitTest {
            dependencies {
                implementation(project(":framework:test-roborazzi"))
            }
        }

        wasmJsMain {
            dependsOn(localDB)

            dependencies {
                implementation("io.ktor:ktor-client-js:_")
            }
        }
    }
}

private val ENV_TEMPLATE_REPLACE_ME_BUILD_VARIABLE = "ENV_TEMPLATE_REPLACE_ME_VARIABLE"

val flyerboardBuildVariable = "hello!"

android {
    namespace = "com.cramsan.flyerboard.client.lib"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        all {
            buildConfigField("String", ENV_TEMPLATE_REPLACE_ME_BUILD_VARIABLE, "\"${flyerboardBuildVariable}\"")
        }
    }
}

compose.resources {
    packageOfResClass = "flyerboard_lib"
}

dependencies {
    // To use Kotlin Symbol Processing (KSP)
    add("kspCommonMainMetadata", "androidx.room3:room3-compiler:_")
    add("kspAndroid", "androidx.room3:room3-compiler:_")
    add("kspJvm", "androidx.room3:room3-compiler:_")

    implementation("androidx.appcompat:appcompat:_")
    implementation("androidx.core:core-ktx:_")
    implementation("androidx.compose.material:material-icons-extended:_")

    implementation("io.coil-kt.coil3:coil:")
    implementation("io.coil-kt.coil3:coil-compose:_")
    implementation("io.coil-kt.coil3:coil-network-ktor3:_")

    implementation("io.ktor:ktor-client-cio:_")

    implementation("androidx.room3:room3-runtime:_")

    implementation("io.insert-koin:koin-core:_")
    implementation("io.insert-koin:koin-android:_")
    implementation("io.insert-koin:koin-androidx-compose:_")
}

room3 {
    schemaDirectory("$projectDir/schemas")
}

tasks.getByName("release") {
    dependsOn("detektLocalDBSourceSet")
}

roborazzi {
    generateComposePreviewRobolectricTests {
        enable = true
        packages = listOf("com.cramsan.flyerboard.client.lib")
    }
}

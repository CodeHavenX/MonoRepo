@file:OptIn(ExperimentalWasmDsl::class, ExperimentalRoborazziApi::class)

import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    id("androidx.room")
    id("com.google.devtools.ksp")
    id("io.github.takahirom.roborazzi")
    id("com.cramsan.kotlin-mpp-common-compose")
    id("com.cramsan.kotlin-mpp-android-lib-compose")
    id("com.cramsan.kotlin-mpp-jvm-compose")
    id("com.cramsan.kotlin-mpp-wasm")
}

kotlin {
    applyDefaultHierarchyTemplate()

    wasmJs {
        browser {}
        binaries.executable()
    }

    sourceSets {
        commonMain {
            kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
        }

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

            implementation(project(":flyerboard:models"))
            implementation(project(":flyerboard:api"))
            implementation(project(":flyerboard:front-end:ui-components"))

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
                implementation("androidx.room:room-runtime:_")
            }
        }

        val noDB by creating {
            dependsOn(commonMain.get())
        }

        jvmMain {
            dependsOn(localDB)

            dependencies {
                implementation("org.apache.logging.log4j:log4j-core:_")
                implementation("org.apache.logging.log4j:log4j-slf4j-impl:_")

                implementation("io.ktor:ktor-client-cio:_")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:_")

                // Room
                implementation("androidx.room:room-runtime:_")
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
        getByName("androidHostTest") {
            dependencies {
                implementation(project(":framework:test-roborazzi"))
            }
        }

        wasmJsMain {
            dependsOn(noDB)

            dependencies {
                implementation("io.ktor:ktor-client-js:_")
            }
        }
    }
}

// WebDestination routing codegen runs against commonMain metadata, but per-target compiles and
// detekt don't depend on that task by default. Without this, a clean build can compile/lint a
// target before MainDestinationWebRoutes/AuthDestinationWebRoutes are generated.
tasks.matching { it.name != "kspCommonMainKotlinMetadata" }.configureEach {
    if (name.startsWith("ksp") || name.startsWith("compile") || name.startsWith("detekt")) {
        dependsOn("kspCommonMainKotlinMetadata")
    }
}

kotlin {
    android {
        namespace = "com.cramsan.flyerboard.client.lib"
    }
}

compose.resources {
    packageOfResClass = "flyerboard_lib"
}

dependencies {
    // To use Kotlin Symbol Processing (KSP)
    add("kspCommonMainMetadata", "androidx.room:room-compiler:_")
    add("kspAndroid", "androidx.room:room-compiler:_")
    add("kspJvm", "androidx.room:room-compiler:_")

    // WebDestination routing codegen (commonMain only — generated routing objects are
    // shared across all targets, no per-platform variants needed)
    add("kspCommonMainMetadata", project(":framework:web-route-ksp"))
}

ksp {
    // Generates FlyerBoardPathNavigation, chaining every WebDestination hierarchy in this
    // module, replacing the hand-maintained PathNavigation.kt aggregator.
    arg("webRouteAggregatorPackage", "com.cramsan.flyerboard.client.lib.navigation")
    arg("webRouteAggregatorName", "FlyerBoardPathNavigation")
}

room {
    schemaDirectory("$projectDir/schemas")
}

tasks.getByName("release") {
    dependsOn("detektLocalDBSourceSet")
    dependsOn("detektNoDBSourceSet")
}

roborazzi {
    generateComposePreviewRobolectricTests {
        enable = true
        packages = listOf("com.cramsan.flyerboard.client.lib")
    }
}

kotlin {
    sourceSets.getByName("androidMain") {
        resources.srcDir("src/commonMain/resources")
    }
}


kotlin {
    sourceSets {
        getByName("androidMain").dependencies {
            implementation("androidx.appcompat:appcompat:_")
            implementation("androidx.core:core-ktx:_")
            implementation("androidx.compose.material:material-icons-extended:_")
            implementation("io.coil-kt.coil3:coil:")
            implementation("io.coil-kt.coil3:coil-compose:_")
            implementation("io.coil-kt.coil3:coil-network-ktor3:_")
            implementation("io.ktor:ktor-client-cio:_")
            implementation("androidx.room:room-runtime:_")
            implementation("androidx.room:room-ktx:_")
            implementation("io.insert-koin:koin-core:_")
            implementation("io.insert-koin:koin-android:_")
            implementation("io.insert-koin:koin-androidx-compose:_")
        }
    }
}
@file:OptIn(ExperimentalWasmDsl::class, ExperimentalRoborazziApi::class)

import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import java.util.Properties
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
    id("com.github.gmazzo.buildconfig")
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
            implementation(project(":framework:core-compose"))
            implementation(project(":framework:configuration"))
            implementation(project(":framework:preferences"))
            implementation(project(":framework:test"))
            implementation(project(":framework:utils"))
            implementation(project(":framework:network-api"))
            implementation(project(":framework:http-serializers"))

            implementation(project(":architecture:front-end-architecture"))

            implementation(project(":ui-catalog"))

            implementation(project(":edifikana:models"))
            implementation(project(":edifikana:api"))
            implementation(project(":edifikana:front-end:ui-components"))

            implementation("org.jetbrains.kotlinx:kotlinx-datetime:_")
            implementation("org.jetbrains.androidx.navigation:navigation-compose:_")
            implementation("org.jetbrains.androidx.lifecycle:lifecycle-runtime-compose:_")
            implementation("org.jetbrains.androidx.lifecycle:lifecycle-viewmodel-compose:_")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:_")

            implementation("io.insert-koin:koin-core:_")
            implementation("io.insert-koin:koin-compose:_")
            implementation("io.insert-koin:koin-compose-viewmodel:_")

            implementation("io.coil-kt.coil3:coil:")
            implementation("io.coil-kt.coil3:coil-compose:_")
            implementation("io.coil-kt.coil3:coil-network-ktor3:_")

            implementation("io.github.jan-tennert.supabase:postgrest-kt:_")
            implementation("io.github.jan-tennert.supabase:storage-kt:_")
            implementation("io.github.jan-tennert.supabase:auth-kt:_")
            implementation("io.github.jan-tennert.supabase:compose-auth:_")
            implementation("io.github.jan-tennert.supabase:compose-auth-ui:_")
            implementation("io.github.jan-tennert.supabase:coil3-integration:_")
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
// target before the generated *WebRoutes objects are generated.
tasks.matching { it.name != "kspCommonMainKotlinMetadata" }.configureEach {
    if (name.startsWith("ksp") || name.startsWith("compile") || name.startsWith("detekt")) {
        dependsOn("kspCommonMainKotlinMetadata")
    }
}

kotlin {
    android {
        namespace = "com.cramsan.edifikana.client.lib"
    }
}

compose.resources {
    packageOfResClass = "edifikana_lib"
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
    // Generates EdifikanaPathNavigation, chaining every WebDestination hierarchy in this
    // module, replacing the hand-maintained PathNavigation.kt aggregator.
    arg("webRouteAggregatorPackage", "com.cramsan.edifikana.client.lib.navigation")
    arg("webRouteAggregatorName", "EdifikanaPathNavigation")
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
        packages = listOf("com.cramsan.edifikana.client.lib")
    }
}

buildConfig {
    packageName("com.cramsan.edifikana.client.lib")
    // config file is not being linked as an input, so changes to config.properties won't reflect with build.gradle
    val buildConfigFile = rootProject.file("edifikana/front-end/config.properties")
    val configProps = Properties().apply {
        if (buildConfigFile.exists()) {
            buildConfigFile.inputStream().use { input ->
                load(input)
            }
        }
    }

    val gradleAppVersion = properties["app.version"]?.toString() ?: "0.0.0"

    buildConfigField<String>(
        "DEFAULT_API_URL",
        configProps.getProperty("DEFAULT_API_URL", "http://127.0.0.1:9292")
    )
    // Accepting risk of silent build failure here with default value being blank
    buildConfigField<String>(
        "GOOGLE_OAUTH_CLIENT_ID",
        configProps.getProperty("GOOGLE_OAUTH_CLIENT_ID", "")
    )
    buildConfigField<String>(
        "APP_VERSION",
        configProps.getProperty("APP_VERSION", gradleAppVersion)
    )
    buildConfigField<String>(
        "BUILD_TYPE",
        configProps.getProperty("BUILD_TYPE", "debug")
    )
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
            implementation("androidx.camera:camera-camera2:_")
            implementation("androidx.camera:camera-lifecycle:_")
            implementation("androidx.camera:camera-view:_")
            implementation("io.coil-kt.coil3:coil:")
            implementation("io.coil-kt.coil3:coil-compose:_")
            implementation("io.coil-kt.coil3:coil-network-ktor3:_")
            implementation("io.ktor:ktor-client-cio:_")
            implementation("androidx.exifinterface:exifinterface:_")
            implementation("androidx.room:room-runtime:_")
            implementation("androidx.room:room-ktx:_")
            implementation("io.insert-koin:koin-core:_")
            implementation("io.insert-koin:koin-android:_")
            implementation("io.insert-koin:koin-androidx-compose:_")
        }
    }
}
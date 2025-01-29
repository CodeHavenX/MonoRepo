import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.android.library")
    id("androidx.room")
    id("com.google.devtools.ksp")
}

apply(from = "$rootDir/gradle/kotlin-mpp-target-common-compose.gradle")
apply(from = "$rootDir/gradle/kotlin-mpp-target-android-lib-compose.gradle")
apply(from = "$rootDir/gradle/kotlin-mpp-target-jvm-compose.gradle")
apply(from = "$rootDir/gradle/kotlin-mpp-target-wasm.gradle")

kotlin {
    wasmJs {
        browser {}
        binaries.executable()
    }

    // Apply the default hierarchy again. It'll create, for example, the iosMain source set:
    applyDefaultHierarchyTemplate()

    sourceSets {
        commonMain.dependencies {
            implementation(project(":framework:assert"))
            implementation(project(":framework:halt"))
            implementation(project(":framework:interfacelib"))
            implementation(project(":framework:logging"))
            implementation(project(":framework:thread"))
            implementation(project(":framework:crashhandler"))
            implementation(project(":framework:core"))
            implementation(project(":framework:core-compose"))
            implementation(project(":framework:preferences"))
            implementation(project(":framework:preferences"))
            implementation(project(":framework:utils"))
            implementation(project(":edifikana:shared"))

            implementation("org.jetbrains.kotlinx:kotlinx-datetime:_")
            implementation("org.jetbrains.androidx.navigation:navigation-compose:_")
            implementation("org.jetbrains.androidx.lifecycle:lifecycle-runtime-compose:_")
            implementation("org.jetbrains.androidx.lifecycle:lifecycle-viewmodel-compose:_")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:_")

            implementation("io.insert-koin:koin-core:_")
            implementation("io.insert-koin:koin-compose:_")

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

        androidMain {
            dependsOn(localDB)
        }

        wasmJsMain {
            dependsOn(noDB)

            dependencies {
                implementation("io.ktor:ktor-client-js:_")
            }
        }
    }
}

private val ENV_EDIFIKANA_SUPABASE_URL = "EDIFIKANA_SUPABASE_URL"
private val ENV_EDIFIKANA_SUPABASE_KEY = "EDIFIKANA_SUPABASE_KEY"

val edifikanaSupabaseUrl = "http://10.0.2.2:54321" // System.getenv(ENV_EDIFIKANA_SUPABASE_URL).orEmpty()
val edifikanaSupabaseKey = System.getenv(ENV_EDIFIKANA_SUPABASE_KEY).orEmpty()

android {
    namespace = "com.cramsan.edifikana.client.lib"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        all {
            buildConfigField("String", ENV_EDIFIKANA_SUPABASE_URL, "\"${edifikanaSupabaseUrl}\"")
            buildConfigField("String", ENV_EDIFIKANA_SUPABASE_KEY, "\"${edifikanaSupabaseKey}\"")
        }
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

room {
    schemaDirectory("$projectDir/schemas")
}
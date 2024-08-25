import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.android.library")
    id("com.google.devtools.ksp")
}

apply(from = "$rootDir/gradle/kotlin-mpp-target-common-compose.gradle")
apply(from = "$rootDir/gradle/kotlin-mpp-target-android-lib-compose.gradle")
apply(from = "$rootDir/gradle/kotlin-mpp-target-jvm-compose.gradle")
apply(from = "$rootDir/gradle/kotlin-mpp-target-wasm.gradle")

kotlin {
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser {}
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":framework:assert"))
            implementation(project(":framework:halt"))
            implementation(project(":framework:interfacelib"))
            implementation(project(":framework:logging"))
            implementation(project(":framework:thread"))
            implementation(project(":framework:preferences"))
            implementation(project(":framework:crashhandler"))
            implementation(project(":framework:core"))

            implementation(project(":alpaca-scheduler:shared"))

            implementation("org.jetbrains.kotlinx:kotlinx-datetime:_")
            implementation("org.jetbrains.androidx.navigation:navigation-compose:_")
            implementation("org.jetbrains.androidx.lifecycle:lifecycle-runtime-compose:_")
            implementation("org.jetbrains.androidx.lifecycle:lifecycle-viewmodel-compose:_")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:_")

            implementation("io.insert-koin:koin-core:_")
            implementation("io.insert-koin:koin-compose:_")

            implementation("io.coil-kt.coil3:coil:")
            implementation("io.coil-kt.coil3:coil-compose:_")
            implementation("io.coil-kt.coil3:coil-network-ktor:_")

            implementation("io.ktor:ktor-client-core:_")
            implementation("io.ktor:ktor-serialization-kotlinx-json:_")

            implementation("io.github.jan-tennert.supabase:postgrest-kt:_")
            implementation("io.github.jan-tennert.supabase:storage-kt:_")
            implementation("io.github.jan-tennert.supabase:gotrue-kt:_")
            implementation("io.github.jan-tennert.supabase:compose-auth:_")
            implementation("io.github.jan-tennert.supabase:compose-auth-ui:_")
        }

        jvmMain.dependencies {
            implementation(project(":alpaca-scheduler:front-end:appcore-db"))

            implementation("org.apache.logging.log4j:log4j-core:_")
            implementation("org.apache.logging.log4j:log4j-slf4j-impl:_")

            implementation("io.ktor:ktor-client-cio:_")

            implementation("androidx.room:room-runtime:_")
            implementation("androidx.sqlite:sqlite-bundled-jvm:_")
        }

        wasmJsMain.dependencies {
            implementation("io.ktor:ktor-client-js:_")
        }
    }
}

android {
    namespace = "com.codehavenx.alpaca.frontend.appcore"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")
}

dependencies {
    implementation(project(":alpaca-scheduler:front-end:appcore-db"))

    implementation("io.insert-koin:koin-android:_")
    implementation("io.insert-koin:koin-androidx-compose:_")

    implementation("io.ktor:ktor-client-cio:_")

    implementation("androidx.room:room-runtime:_")
    implementation("androidx.room:room-ktx:_")
}

compose.resources {
    packageOfResClass = "shared_compose"
}

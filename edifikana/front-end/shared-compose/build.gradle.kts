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

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":framework:assert"))
            implementation(project(":framework:halt"))
            implementation(project(":framework:interfacelib"))
            implementation(project(":framework:logging"))
            implementation(project(":framework:thread"))
            implementation(project(":framework:crashhandler"))
            implementation(project(":framework:core"))

            implementation(project(":edifikana:shared"))

            implementation("org.jetbrains.kotlinx:kotlinx-datetime:_")
            implementation("org.jetbrains.androidx.navigation:navigation-compose:_")
            implementation("org.jetbrains.androidx.lifecycle:lifecycle-runtime-compose:_")
            implementation("org.jetbrains.androidx.lifecycle:lifecycle-viewmodel-compose:_")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:_")
            implementation("androidx.room:room-runtime:_")

            implementation("io.insert-koin:koin-core:_")
            implementation("io.insert-koin:koin-compose:_")

            implementation("io.coil-kt.coil3:coil:")
            implementation("io.coil-kt.coil3:coil-compose:_")
            implementation("io.coil-kt.coil3:coil-network-ktor:_")
            implementation("io.ktor:ktor-client-android:_")

            implementation("io.github.jan-tennert.supabase:postgrest-kt:_")
            implementation("io.github.jan-tennert.supabase:storage-kt:_")
            implementation("io.github.jan-tennert.supabase:gotrue-kt:_")
            implementation("io.github.jan-tennert.supabase:compose-auth:_")
            implementation("io.github.jan-tennert.supabase:compose-auth-ui:_")
            // implementation("io.github.jan-tennert.supabase:imageloader-integration:_")
        }

        jvmMain.dependencies {
            implementation("io.ktor:ktor-client-cio:_")
        }
    }
}

android {
    namespace = "com.cramsan.edifikana.client.lib"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")
}

compose.resources {
    packageOfResClass = "edifikana_lib"
}

dependencies {
    // To use Kotlin Symbol Processing (KSP)
    add("kspCommonMainMetadata", "androidx.room:room-compiler:_")
    add("kspAndroid", "androidx.room:room-compiler:_")
    add("kspJvm", "androidx.room:room-compiler:_")

    implementation("io.insert-koin:koin-android:_")
    implementation("io.insert-koin:koin-androidx-compose:_")
    implementation("io.ktor:ktor-client-cio:_")
}

room {
    schemaDirectory("$projectDir/schemas")
}
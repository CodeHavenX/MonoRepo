plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("com.android.library")
}

apply(from = "$rootDir/gradle/kotlin-mpp-target-common-compose.gradle")
apply(from = "$rootDir/gradle/kotlin-mpp-target-android-lib-compose.gradle")
apply(from = "$rootDir/gradle/kotlin-mpp-target-jvm-compose.gradle")

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":framework:interfacelib"))
            implementation(project(":framework:logging"))
            implementation(project(":edifikana:shared"))

            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0-RC.2")
            implementation("org.jetbrains.androidx.navigation:navigation-compose:2.8.0-alpha01")
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
plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("androidx.room")
    id("com.google.devtools.ksp")
}

apply(from = "$rootDir/gradle/kotlin-mpp-target-common.gradle")
apply(from = "$rootDir/gradle/kotlin-mpp-target-android-lib.gradle")
apply(from = "$rootDir/gradle/kotlin-mpp-target-jvm.gradle")

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("androidx.room:room-runtime:_")
        }
    }
}

android {
    namespace = "com.codehavenx.alpaca.frontend.appcore.database"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")
}

room {
    schemaDirectory("$projectDir/schemas")
}

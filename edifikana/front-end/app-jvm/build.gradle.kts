import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

apply(from = "$rootDir/gradle/kotlin-jvm-target-lib-compose.gradle")

dependencies {
    implementation(project(":edifikana:front-end:shared-compose"))
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.cramsan.edifikana.client.desktop"
            packageVersion = "1.0.0"
        }
    }
}
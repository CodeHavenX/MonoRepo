import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
    id("com.cramsan.kotlin-jvm-lib-compose")
}

dependencies {
    implementation(project(":samples:jbcompose-mpp-lib"))
}

compose.desktop {
    application {
        mainClass = "com.cramsan.sample.compose.jvm.WindowKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "KotlinMultiplatformComposeDesktopApplication"
            packageVersion = "1.0.0"
        }
    }
}

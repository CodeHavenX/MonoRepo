import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
}

apply(from = "$rootDir/gradle/kotlin-jvm-target-lib-compose.gradle")

dependencies {
    implementation(project(":framework-samples:framework-sample-app"))

    implementation("io.insert-koin:koin-core:_")
    implementation("io.insert-koin:koin-compose:_")
}

compose.desktop {
    application {
        mainClass = "com.cramsan.framework.samples.desktop.ApplicationKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.cramsan.framework.samples.desktop"
            packageVersion = "1.0.0"
        }
    }
}

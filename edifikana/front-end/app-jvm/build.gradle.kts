import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
}

apply(from = "$rootDir/gradle/kotlin-jvm-target-lib-compose.gradle")

dependencies {
    implementation(project(":edifikana:front-end:shared-compose"))

    implementation("io.insert-koin:koin-core:_")
    implementation("io.insert-koin:koin-compose:_")
}

compose.desktop {
    application {
        mainClass = "com.cramsan.edifikana.client.desktop.EdifikanaApplicationKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.cramsan.edifikana.client.desktop"
            packageVersion = "1.0.0"
        }
    }
}

// https://github.com/JetBrains/compose-multiplatform/issues/4711
// Temporary mitigation for the issue above regarding the latest versions of Compose MP and Jetpack Navigation
configurations.all {
    resolutionStrategy {
        force(AndroidX.compose.material.ripple) }
}
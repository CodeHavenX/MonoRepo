import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
}

apply(from = "$rootDir/gradle/kotlin-jvm-target-lib-compose.gradle")

dependencies {
    implementation(project(":framework:core-compose"))

    implementation(project(":runasimi:front-end:shared-app"))

    implementation("io.insert-koin:koin-core:_")
    implementation("io.insert-koin:koin-compose:_")
    implementation("io.insert-koin:koin-compose-viewmodel:_")
}

compose.desktop {
    application {
        mainClass = "com.cramsan.runasimi.client.desktop.RunasimiApplicationKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.cramsan.runasimi.client.desktop"
            packageVersion = "1.0.0"
        }
    }
}

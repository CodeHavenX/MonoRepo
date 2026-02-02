import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
}

apply(from = "$rootDir/gradle/kotlin-jvm-target-lib-compose.gradle")

dependencies {
    implementation(project(":framework:core"))
    implementation(project(":framework:core-compose"))

    implementation(project(":edifikana:front-end:shared-app"))

    implementation("io.insert-koin:koin-core:_")
    implementation("io.insert-koin:koin-compose:_")
    implementation("io.insert-koin:koin-compose-viewmodel:_")
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

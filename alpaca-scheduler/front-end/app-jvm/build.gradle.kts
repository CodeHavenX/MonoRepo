import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

apply(from = "$rootDir/gradle/kotlin-jvm-target-lib-compose.gradle")

dependencies {
    implementation(project(":alpaca-scheduler:front-end:appcore"))

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:_")

    implementation("io.insert-koin:koin-core:_")
    implementation("io.insert-koin:koin-compose:_")
}

compose.desktop {
    application {
        mainClass = "com.codehavenx.alpaca.frontend.desktop.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.codehavenx.alpaca.frontend.desktop"
            packageVersion = "1.0.0"
        }
    }
}
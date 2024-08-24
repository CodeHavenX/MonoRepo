import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

apply(from = "$rootDir/gradle/kotlin-jvm-target-lib-compose.gradle")

dependencies {
    implementation(project(":framework:assert"))
    implementation(project(":framework:halt"))
    implementation(project(":framework:interfacelib"))
    implementation(project(":framework:logging"))
    implementation(project(":framework:thread"))
    implementation(project(":framework:crashhandler"))
    implementation(project(":framework:core"))
    implementation(project(":framework:preferences"))

    implementation(project(":alpaca-scheduler:front-end:appcore"))

    implementation("org.jetbrains.kotlinx:kotlinx-datetime:_")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:_")

    implementation("org.apache.logging.log4j:log4j-core:_")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:_")

    implementation("io.insert-koin:koin-core:_")
    implementation("io.insert-koin:koin-compose:_")

    // Room
    implementation("androidx.room:room-runtime:_")
    implementation("androidx.sqlite:sqlite-bundled-jvm:_")
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
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import de.fayard.refreshVersions.core.versionFor

plugins {
    id("org.jetbrains.compose")
    kotlin("jvm")
}

apply(from = "$rootDir/gradle/kotlin-jvm-lib.gradle")

kotlin {
    jvmToolchain(17)
}

compose.desktop {
    application {
        mainClass = "com.cramsan.framework.sample.jvm.FrameworkSampleJvmAppKt"
    }
}

dependencies {
    implementation(compose.desktop.currentOs)

    implementation(project(":framework:assert"))
    implementation(project(":framework:core"))
    implementation(project(":framework:halt"))
    implementation(project(":framework:interfacelib"))
    implementation(project(":framework:logging"))
    implementation(project(":framework:preferences"))
    implementation(project(":framework:thread"))
    implementation(project(":framework:interfacelib"))

    implementation("io.insert-koin:koin-compose:${versionFor("version.io.insert-koin..koin-compose")}")
    implementation("io.insert-koin:koin-core:_")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:_")
}
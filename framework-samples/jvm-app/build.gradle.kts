import org.jetbrains.compose.desktop.application.dsl.TargetFormat

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
        mainClass = "com.cramsan.framework.sample.jvm.MainKt"
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

    implementation("io.insert-koin:koin-core:3.4.2")
    implementation("io.insert-koin:koin-compose:1.0.3")

    implementation(KotlinX.coroutines.swing)
}
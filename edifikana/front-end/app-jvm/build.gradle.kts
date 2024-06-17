import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
    kotlin("plugin.serialization")
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

    implementation(project(":edifikana:front-end:shared-compose"))
    implementation(project(":edifikana:shared"))

    implementation("org.jetbrains.kotlinx:kotlinx-datetime:_")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:_")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:_")

    implementation("org.apache.logging.log4j:log4j-core:_")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:_")

    implementation("io.insert-koin:koin-core:_")
    implementation("io.insert-koin:koin-compose:_")

    // Room
    implementation("androidx.room:room-runtime:_")
    implementation("androidx.sqlite:sqlite-bundled-jvm:2.5.0-alpha04")

    implementation("dev.gitlive:firebase-auth:1.12.0")
    implementation("dev.gitlive:firebase-firestore:1.12.0")
    implementation("dev.gitlive:firebase-storage:1.14.0")
    implementation("dev.gitlive:firebase-installations:1.12.0")
    implementation("dev.gitlive:firebase-config:1.12.0")
    implementation("dev.gitlive:firebase-perf:1.12.0")
    implementation("dev.gitlive:firebase-java-sdk:0.4.3")
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
        force("androidx.compose.material:material-ripple:1.7.0-alpha05") }
}
import de.fayard.refreshVersions.core.versionFor

plugins {
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    kotlin("jvm")
}

apply(from = "$rootDir/gradle/kotlin-jvm-target-lib-compose.gradle")

kotlin {
    jvmToolchain(17)
}

compose.desktop {
    application {
        mainClass = "com.cramsan.framework.sample.jvm.FrameworkSampleJvmAppKt"
    }
}

dependencies {
    implementation(project(":framework:assert"))
    implementation(project(":framework:core"))
    implementation(project(":framework:halt"))
    implementation(project(":framework:interfacelib"))
    implementation(project(":framework:logging"))
    implementation(project(":framework:preferences"))
    implementation(project(":framework:thread"))
    implementation(project(":framework:interfacelib"))

    implementation("io.insert-koin:koin-compose:${versionFor("version.io.insert-koin")}")
    implementation("io.insert-koin:koin-core:_")
    implementation("org.apache.logging.log4j:log4j-slf4j2-impl:_")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:_")
}
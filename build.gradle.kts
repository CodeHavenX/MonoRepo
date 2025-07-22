plugins {
    // Temporarily commenting out Android plugins due to firewall restrictions
    // id("com.android.application") apply false
    // id("com.android.library") apply false
    kotlin("jvm") apply false
    kotlin("multiplatform") apply false
    // id("com.google.devtools.ksp") apply false
    // id("com.google.dagger.hilt.android") apply false
    id("org.jetbrains.compose") apply false
    id("org.jetbrains.kotlin.plugin.compose") apply false
    kotlin("plugin.serialization") apply false
    // id("androidx.navigation.safeargs.kotlin") apply false
    // id("com.squareup.sqldelight") apply false
    id("io.gitlab.arturbosch.detekt") apply false
    // id("io.github.takahirom.roborazzi") apply false
}

subprojects {
    apply(from = "$rootDir/gradle/detekt.gradle")
}

/**
 * Production task settings for all projects. These must pass to consider
 * all projects are running correctly.
 */
tasks.register("releaseAll") {
    group = "release"
    description = "Builds all target"

    dependsOn("framework:assert:release")
    dependsOn("framework:configuration:release")
    dependsOn("framework:crashhandler:release")
    dependsOn("framework:core:release")
    dependsOn("framework:core-compose:release")
    dependsOn("framework:core-ktor:release")
    dependsOn("framework:halt:release")
    dependsOn("framework:interfacelib:release")
    dependsOn("framework:interfacelib-test:release")
    dependsOn("framework:logging:release")
    dependsOn("framework:metrics:release")
    dependsOn("framework:userevents:release")
    dependsOn("framework:preferences:release")
    dependsOn("framework:thread:release")
    dependsOn("framework:test:release")
    // Temporarily including only pure JVM modules that don't depend on Android due to firewall restrictions
    dependsOn("framework:core-ktor:release")
    dependsOn("samples:jvm-lib:release")
    dependsOn("samples:nodejs-app:release")
    dependsOn("samples:service-ktor:release")
}

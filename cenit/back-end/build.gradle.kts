plugins {
    kotlin("plugin.serialization")
    id("io.ktor.plugin")
}

val mainClassTarget by extra("com.cramsan.cenit.server.ApplicationKt")

// Configures default settings for JVM project
apply(from = "$rootDir/gradle/kotlin-jvm-target-application.gradle")

dependencies {
    implementation(project(":cenit:shared"))

    implementation ("com.google.api-client:google-api-client:2.4.1")
    implementation ("com.google.auth:google-auth-library-credentials:1.23.0")
    implementation ("com.google.apis:google-api-services-drive:v3-rev20220815-2.0.0")
    implementation ("com.google.apis:google-api-services-sheets:v4-rev20240416-2.0.0")
}

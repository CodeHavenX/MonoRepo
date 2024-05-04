plugins {
    kotlin("plugin.serialization")
    id("io.ktor.plugin")
}

val mainClassTarget by extra("com.cramsan.edifikana.server.ApplicationKt")

// Configures default settings for JVM project
apply(from = "$rootDir/gradle/kotlin-jvm-target-application.gradle")

dependencies {
    implementation(project(":edifikana:shared"))

    implementation("com.google.api-client:google-api-client:2.4.1")
    implementation("com.google.auth:google-auth-library-credentials:1.23.0")
    implementation("com.google.apis:google-api-services-drive:v3-rev20220815-2.0.0")
    implementation("com.google.apis:google-api-services-sheets:v4-rev20240416-2.0.0")
//    implementation("com.google.cloud:google-cloud-firestore:3.21.0")
    implementation("com.google.cloud.functions:functions-framework-api:1.0.4")
    implementation("com.google.cloud:google-cloudevent-types:0.7.0")
//    implementation("com.google.protobuf:protobuf-java:3.22.2")

    // Firestore Dependencies
//    implementation("com.google.firebase:firebase-firestore:24.11.1")
    implementation("com.google.firebase:firebase-admin:9.2.0")
//    implementation("com.google.firebase:firebase-common-ktx:21.0.0")

    // Other Dependencies
//    implementation("io.grpc:grpc-core:1.63.0")
}

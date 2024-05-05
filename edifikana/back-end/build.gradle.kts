import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

val mainClassTarget by extra("com.cramsan.edifikana.server.CloudFireControllerKt")

// Configures default settings for JVM project
apply(from = "$rootDir/gradle/kotlin-jvm-target-application.gradle")

dependencies {
    implementation(project(":edifikana:shared"))

    implementation("com.google.apis:google-api-services-drive:v3-rev20231120-2.0.0")
    implementation("com.google.apis:google-api-services-sheets:v4-rev20230815-2.0.0")

    implementation("com.google.cloud.functions:functions-framework-api:1.0.4")
    implementation("com.google.cloud:google-cloudevent-types:0.7.0")

    // Firestore Dependencies
    implementation("com.google.firebase:firebase-admin:9.2.0")

    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0-RC.2")
}

tasks.withType<ShadowJar> {
    mergeServiceFiles()
}

tasks.named("build") {
    dependsOn("shadowJar")
}
tasks.register<Copy>("buildFunction") {
    group = "build"
    dependsOn("build")
    from("$projectDir/build/libs/" + projectDir.name + "-all.jar")
    into("$projectDir/build/deploy")
}
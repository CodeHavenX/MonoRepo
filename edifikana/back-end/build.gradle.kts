import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("com.github.johnrengelman.shadow")
}

val mainClassTarget by extra("com.cramsan.edifikana.server.CloudFirebaseAppKt")

// Configures default settings for JVM project
apply(from = "$rootDir/gradle/kotlin-jvm-target-application.gradle")

dependencies {
    implementation(project(":edifikana:shared"))

    implementation("com.google.apis:google-api-services-drive:_")
    implementation("com.google.apis:google-api-services-sheets:_")

    implementation("com.google.cloud.functions:functions-framework-api:_")
    implementation("com.google.cloud:google-cloudevent-types:_")

    implementation("io.cloudevents:cloudevents-core:2.3.0")
    implementation("io.cloudevents:cloudevents-json-jackson:2.3.0")

    // Ktor Dependencies
    implementation("io.ktor:ktor-server-core-jvm:_")
    implementation("io.ktor:ktor-server-netty-jvm:_")

    // Firestore Dependencies
    implementation("com.google.firebase:firebase-admin:_")

    implementation("org.jetbrains.kotlinx:kotlinx-datetime:_")
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

tasks.register("strictBuildFunction") {
    group = "build"

    dependsOn("buildFunction")

    System.getenv("EDIFIKANA_PROJECT_NAME").ifBlank {
        TODO("Environment variable EDIFIKANA_PROJECT_NAME not set")
    }
    System.getenv("EDIFIKANA_STORAGE_FOLDER_ID").ifBlank {
        TODO("Environment variable EDIFIKANA_STORAGE_FOLDER_ID not set")
    }
    System.getenv("EDIFIKANA_TIME_CARD_SPREADSHEET_ID").ifBlank {
        TODO("Environment variable EDIFIKANA_TIME_CARD_SPREADSHEET_ID not set")
    }
    System.getenv("EDIFIKANA_FORM_ENTRIES_SPREADSHEET_ID").ifBlank {
        TODO("Environment variable EDIFIKANA_FORM_ENTRIES_SPREADSHEET_ID not set")
    }
    System.getenv("EDIFIKANA_EVENT_LOG_SPREADSHEET_ID").ifBlank {
        TODO("Environment variable EDIFIKANA_EVENT_LOG_SPREADSHEET_ID not set")
    }
}
package com.cramsan

plugins {
    id("application")
    id("io.ktor.plugin")
    id("com.cramsan.kotlin-jvm-application")
}

afterEvaluate {
    val jarNameTarget = project.findProperty("jarNameTarget")?.toString()?.takeIf { it.isNotBlank() }
        ?: throw GradleException("Missing required property 'jarNameTarget' in project '${project.name}'.")

    ktor {
        fatJar {
            archiveFileName.set("${jarNameTarget}-all.jar")
        }
    }

    tasks.named("releaseJvm") {
        dependsOn("buildFatJar")
    }
}

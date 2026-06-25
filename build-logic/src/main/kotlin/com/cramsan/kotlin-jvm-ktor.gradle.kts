package com.cramsan

import org.gradle.api.tasks.JavaExec

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

    // Local dev mode (hot reload, verbose errors) lives in application.debug.conf, layered on
    // top of the base application.conf. Wire it into `run` automatically so the Gradle task
    // behaves the same way as the checked-in IntelliJ run configurations.
    if (file("src/main/resources/application.debug.conf").exists()) {
        tasks.named<JavaExec>("run") {
            args("-config=application.conf", "-config=application.debug.conf")
        }
    }

    val isCiDeployable = project.extra.has("ciDeployable") && project.extra["ciDeployable"] == true
    if (isCiDeployable) {
        val relPath = project.path.replace(':', '/').trimStart('/')
        val artifactPath = "$relPath/build/libs/${jarNameTarget}-all.jar"
        tasks.register("writeCIArtifactPath") {
            group = "ci"
            val outputFile = project.layout.buildDirectory.file("ci-artifact-path.txt")
            outputs.file(outputFile)
            doLast { outputFile.get().asFile.writeText(artifactPath) }
        }
        tasks.named("releaseJvm") { dependsOn("writeCIArtifactPath") }
    }
}

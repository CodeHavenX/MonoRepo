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

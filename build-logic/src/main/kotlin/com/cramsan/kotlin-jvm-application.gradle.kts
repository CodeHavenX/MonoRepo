package com.cramsan

plugins {
    id("application")
    id("com.cramsan.kotlin-jvm-lib")
}

afterEvaluate {
    val jarNameTarget = project.findProperty("jarNameTarget")?.toString()?.takeIf { it.isNotBlank() }
        ?: throw GradleException(
            "Missing required property 'jarNameTarget' in project '${project.name}'.\n" +
            "Define it before the plugins {} block:\n\n" +
            "    val jarNameTarget by extra(\"my-app\")\n"
        )

    val mainClassTarget = project.findProperty("mainClassTarget")?.toString()?.takeIf { it.isNotBlank() }
        ?: throw GradleException(
            "Missing required property 'mainClassTarget' in project '${project.name}'.\n" +
            "Define it before the plugins {} block:\n\n" +
            "    val mainClassTarget by extra(\"com.example.MainKt\")\n"
        )

    tasks.named<Jar>("jar") {
        archiveBaseName.set(jarNameTarget)
        archiveVersion.set("")
        archiveClassifier.set("")
        manifest {
            attributes("Main-Class" to mainClassTarget)
        }
    }

    extensions.configure<JavaApplication> {
        mainClass.set(mainClassTarget)
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

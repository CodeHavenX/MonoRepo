package com.cramsan

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.cramsan.kotlin-mpp-wasm")
}

afterEvaluate {
    val wasmModuleName = project.findProperty("wasmModuleName")?.toString()?.takeIf { it.isNotBlank() }
        ?: throw GradleException(
            "wasmModuleName must be set and not empty in your build script.\n" +
            "Ensure that you have this line BEFORE the plugins {} block:\n\n" +
            "    val wasmModuleName by extra(\"YOUR_MODULE_NAME\")\n"
        )

    tasks.register<Zip>("zipWasmProductionExecutable") {
        group = "distribution"
        description = "Zips the WASM production executable output."
        from("build/dist/wasmJs/productionExecutable")
        archiveFileName.set("$wasmModuleName.zip")
        destinationDirectory.set(file("build/dist/wasmJs/"))
        dependsOn("wasmJsBrowserDistribution")
    }

    tasks.named("releaseWasm") {
        dependsOn("zipWasmProductionExecutable")
    }

    val isCiDeployable = project.extra.has("ciDeployable") && project.extra["ciDeployable"] == true
    if (isCiDeployable) {
        val relPath = project.path.replace(':', '/').trimStart('/')
        val artifactPath = "$relPath/build/dist/wasmJs/"
        tasks.register("writeCIArtifactPath") {
            group = "ci"
            val outputFile = project.layout.buildDirectory.file("ci-artifact-path.txt")
            outputs.file(outputFile)
            doLast { outputFile.get().asFile.writeText(artifactPath) }
        }
        tasks.named("releaseWasm") { dependsOn("writeCIArtifactPath") }
    }
}

package com.cramsan

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.cramsan.kotlin-mpp-wasm")
}

afterEvaluate {
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
        tasks.named("releaseWasm") {
            dependsOn("writeCIArtifactPath")
            dependsOn("wasmJsBrowserDistribution")
        }
    }
}

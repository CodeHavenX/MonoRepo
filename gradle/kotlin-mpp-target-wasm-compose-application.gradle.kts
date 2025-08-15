/**
 * Configure a Kotlin-wasm target with compose support.
 */

apply(plugin = "org.jetbrains.kotlin.multiplatform")
apply(plugin = "org.jetbrains.compose")
apply(plugin = "org.jetbrains.kotlin.plugin.compose")

apply(from = "$rootDir/gradle/kotlin-mpp-target-wasm.gradle.kts")

compose.experimental {
    web.application {}
}

if (!project.hasProperty("wasmModuleName") || 
    project.property("wasmModuleName") == null || 
    project.property("wasmModuleName").toString().trim().isEmpty()) {
    throw GradleException(
        "wasmModuleName must be set and not empty in your build script.\n" +
        "Ensure that you have this line BEFORE you apply any wasm-compose plugins:\n" +
        "\tval wasmModuleName by extra(\"YOUR_MODULE_NAME\")\n"
    )
}

tasks.register<Zip>("zipWasmProductionExecutable") {
    group = "distribution"
    description = "Zips the WASM production executable output."
    from("build/dist/wasmJs/productionExecutable")
    archiveFileName.set(project.property("wasmModuleName").toString() + ".zip")
    destinationDirectory.set(file("build/dist/wasmJs/"))

    dependsOn("wasmJsBrowserDistribution")
}

tasks.named("releaseWasm").configure {
    dependsOn("zipWasmProductionExecutable")
}
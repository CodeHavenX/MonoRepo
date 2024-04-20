import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("com.android.library")
    id("org.openapi.generator")
}

apply(from = "$rootDir/gradle/kotlin-mpp-target-common-compose.gradle")
apply(from = "$rootDir/gradle/kotlin-mpp-target-android-compose.gradle")
apply(from = "$rootDir/gradle/kotlin-mpp-target-jvm-compose.gradle")
// TODO: Enable WASM target for Alpaca Scheduler #24
// apply(from = "$rootDir/gradle/kotlin-mpp-target-wasm.gradle")

kotlin {
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser {}
        binaries.executable()
    }

    sourceSets {
        commonMain {
            kotlin.srcDirs(
                "src/commonMain/kotlin",
                "src/generated/kotlin",
            )

            dependencies {
                implementation(project(":framework:interfacelib"))
                implementation(project(":framework:logging"))
                implementation(project(":alpaca-scheduler:shared"))
            }
        }
    }
}

android {
    namespace = "org.example.project"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")
}

compose.resources {
    packageOfResClass = "shared_compose"
}

val openApiOutputDir = "${buildDir.absolutePath}/generated/openapi"
val generatedSourceSet = "${projectDir.absolutePath}/src/generated/"

openApiGenerate {
    generatorName = "kotlin"
    inputSpec = "${projectDir}/../../openapi/alpaca-scheduler-api.yaml"
    outputDir = openApiOutputDir
    groupId = "com.codehavenx.alpaca.server"
    packageName = "com.codehavenx.alpaca.client"
    apiPackage = "com.codehavenx.alpaca.client.api"
    modelPackage = "com.codehavenx.alpaca.client.model"
    configOptions = mapOf(
        "dateLibrary" to "kotlinx-datetime",
        "library" to "multiplatform",
        "omitGradlePluginVersions" to "false",
        "omitGradleWrapper" to "false",
        "useSettingsGradle" to "false",
    )
}

val cleanGeneratedCode = tasks.register("cleanGeneratedCode", Delete::class) {
    delete = setOf(openApiOutputDir, generatedSourceSet)
}
val moveGeneratedCodeTask = tasks.register("moveGeneratedCode", Exec::class) {
    group = "build"
    commandLine = listOf(
        "bash",
        "${projectDir.absolutePath}/../../tools/move_generated_code.sh",
        projectDir.absolutePath,
        openApiOutputDir,
        generatedSourceSet,
    )
}

tasks.openApiGenerate {
    dependsOn(cleanGeneratedCode)
    finalizedBy(moveGeneratedCodeTask)
}

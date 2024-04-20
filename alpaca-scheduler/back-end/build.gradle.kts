plugins {
    kotlin("plugin.serialization")
    id("io.ktor.plugin")
    kotlin("jvm")
    id("org.openapi.generator")
}

val mainClassTarget by extra("io.ktor.server.netty.EngineMain")

// Configures default settings for JVM project
apply(from = "$rootDir/gradle/kotlin-jvm-application.gradle")

sourceSets {
    main {
        java {
            srcDirs(
                "src/main/kotlin",
                "src/generated/kotlin",
            )
        }
    }
}

dependencies {
    implementation(project(":framework:interfacelib"))
    implementation(project(":framework:logging"))
    implementation(project(":framework:assert"))
    implementation(project(":framework:core"))
    implementation(project(":framework:core-ktor"))
    implementation(project(":framework:halt"))
    implementation(project(":framework:preferences"))
    implementation(project(":framework:thread"))

    implementation(project(":alpaca-scheduler:shared"))

    implementation("io.ktor:ktor-server-auth:_")
    implementation("io.ktor:ktor-server-core-jvm:_")
    implementation("io.ktor:ktor-server-cors:_")
    implementation("io.ktor:ktor-server-netty-jvm:_")
    implementation("io.ktor:ktor-server-call-logging:_")
    implementation("io.ktor:ktor-server-content-negotiation:_")
    implementation("io.ktor:ktor-serialization-kotlinx-json:_")
    implementation("io.ktor:ktor-server-auto-head-response:_")
    implementation("io.ktor:ktor-server-default-headers:_")
    implementation("io.ktor:ktor-server-content-negotiation:_")
    implementation("io.ktor:ktor-server-resources:_")
    implementation("io.ktor:ktor-server-hsts:_")
    implementation("io.ktor:ktor-server-compression:_")
    implementation("io.ktor:ktor-server-metrics:_")

    implementation("dev.kord:kord-core:_")
    implementation("io.insert-koin:koin-core:_")
    implementation("io.insert-koin:koin-ktor:_")
    implementation("org.apache.logging.log4j:log4j-core:_")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:_")
    implementation("org.mongodb:mongodb-driver-kotlin-coroutine:4.11.0")

    testImplementation("io.ktor:ktor-server-tests-jvm:_")
    testImplementation("io.ktor:ktor-server-test-host:_")
    testImplementation(project(":framework:test"))
}

// Configures the distribution archives, excluding duplicate files
val distTar by tasks.getting(Tar::class) {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

val distZip by tasks.getting(Zip::class) {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

val openApiOutputDir = "${buildDir.absolutePath}/generated/openapi"
val generatedSourceSet = "${projectDir.absolutePath}/src/generated/"

openApiGenerate {
    generatorName = "kotlin-server"
    inputSpec = "${projectDir}/../openapi/alpaca-scheduler-api.yaml"
    outputDir = openApiOutputDir
    apiPackage = "com.codehavenx.alpaca.server.api"
    modelPackage = "com.codehavenx.alpaca.server.model"
    configOptions = mapOf(
        "groupId" to "com.codehavenx.alpaca.server",
        "packageName" to "com.codehavenx.alpaca.server",
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
        "${projectDir.absolutePath}/../tools/move_generated_code.sh",
        projectDir.absolutePath,
        openApiOutputDir,
        generatedSourceSet,
    )
}

tasks.openApiGenerate {
    dependsOn(cleanGeneratedCode)
    finalizedBy(moveGeneratedCodeTask)
}

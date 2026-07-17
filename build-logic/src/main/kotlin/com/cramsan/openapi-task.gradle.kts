package com.cramsan

import org.gradle.api.tasks.SourceSetContainer
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JavaToolchainService
import java.io.File
import java.net.ServerSocket
import java.net.URI
import java.util.concurrent.TimeUnit

// Boots the Ktor server in a throwaway subprocess, downloads its runtime-generated
// OpenAPI spec, and writes it into the repo. This keeps the committed spec scalable to
// any number of back-end apps: each just applies this plugin, no per-app orchestration code.
afterEvaluate {
    val mainClassTarget = project.findProperty("mainClassTarget")?.toString()?.takeIf { it.isNotBlank() }
        ?: throw GradleException(
            "Missing required property 'mainClassTarget' in project '${project.name}'.\n" +
            "Define it before the plugins {} block:\n\n" +
            "    val mainClassTarget by extra(\"com.example.MainKt\")\n"
        )

    val openApiOutputTarget = project.findProperty("openApiOutputTarget")?.toString()?.takeIf { it.isNotBlank() }
        ?: "docs/openapi.yaml"

    val runtimeClasspath = project.extensions.getByType(SourceSetContainer::class.java)
        .getByName("main")
        .runtimeClasspath
    val outputFile = project.layout.projectDirectory.file(openApiOutputTarget).asFile
    val javaLauncherPath = project.extensions.getByType<JavaToolchainService>()
        .launcherFor { languageVersion.set(JavaLanguageVersion.of(21)) }
        .get()
        .executablePath
        .asFile
        .absolutePath

    val generateOpenApiSpec = tasks.register("generateOpenApiSpec") {
        group = "documentation"
        description = "Boots the server, downloads its generated OpenAPI spec, and writes it to $openApiOutputTarget."
        dependsOn("classes")

        doLast {
            val configDir = temporaryDir
            File(configDir, "config.properties").writeText(
                // Values only need to be non-blank: the Supabase client is never eagerly
                // connected during boot, so dummy values let the server come up standalone.
                """
                supabase.url=http://127.0.0.1:54321
                supabase.key=dummy-key-for-openapi-generation
                """.trimIndent(),
            )
            val logFile = File(configDir, "server.log")
            val port = ServerSocket(0).use { it.localPort }

            val process = ProcessBuilder(javaLauncherPath, "-cp", runtimeClasspath.asPath, mainClassTarget)
                .directory(configDir)
                .redirectErrorStream(true)
                .redirectOutput(logFile)
                .apply { environment()["PORT"] = port.toString() }
                .start()

            try {
                val specUrl = URI("http://127.0.0.1:$port/swaggerUI/documentation.yaml").toURL()
                val deadlineMs = System.currentTimeMillis() + 30_000
                var content: String? = null
                var lastError: Exception? = null
                while (content == null && System.currentTimeMillis() < deadlineMs) {
                    if (!process.isAlive) {
                        throw GradleException(
                            "Server process for '${project.name}' exited early (code=${process.exitValue()}) " +
                            "before serving the OpenAPI spec. See $logFile for details.",
                        )
                    }
                    content = try {
                        specUrl.readText()
                    } catch (e: Exception) {
                        lastError = e
                        Thread.sleep(300)
                        null
                    }
                }
                requireNotNull(content) {
                    "Timed out waiting for the OpenAPI spec at $specUrl. Last error: $lastError. " +
                    "See $logFile for the server's output."
                }
                outputFile.parentFile.mkdirs()
                outputFile.writeText(content)
                logger.lifecycle("Wrote OpenAPI spec to $outputFile")
            } finally {
                process.destroy()
                if (!process.waitFor(5, TimeUnit.SECONDS)) {
                    process.destroyForcibly()
                }
            }
        }
    }

    tasks.named("release") {
        dependsOn(generateOpenApiSpec)
    }
}

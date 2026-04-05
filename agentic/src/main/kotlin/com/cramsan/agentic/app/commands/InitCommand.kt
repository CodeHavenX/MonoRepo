package com.cramsan.agentic.app.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.cramsan.agentic.core.AgenticConfig
import com.cramsan.agentic.core.VcsProviderConfig
import com.cramsan.agentic.input.DefaultScaffolder
import com.cramsan.framework.logging.logD
import com.cramsan.framework.logging.logI
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.nio.file.Files
import java.nio.file.Path

private const val TAG = "InitCommand"

class InitCommand : CliktCommand(name = "init", help = "Scaffold input docs and write default config.json") {

    private val outputDir by option("--dir", help = "Output directory for docs").default(".agentic")

    override fun run() {
        logI(TAG, "Init started. Output directory: $outputDir")

        val dir = Path.of(outputDir)
        logD(TAG, "Creating agentic directory: $dir")
        Files.createDirectories(dir)

        val docsDir = dir.resolve("docs")
        logD(TAG, "Creating docs directory: $docsDir")
        Files.createDirectories(docsDir)

        logI(TAG, "Scaffolding default documents into: $docsDir")
        val scaffolder = DefaultScaffolder()
        scaffolder.scaffold(docsDir)

        logD(TAG, "Building default AgenticConfig")
        val config = AgenticConfig(
            agentPoolSize = 2,
            baseBranch = "main",
            docsDir = docsDir.toString(),
            vcsProvider = VcsProviderConfig.GitHub(owner = "your-org", repo = "your-repo"),
        )
        val json = Json { prettyPrint = true }
        val configJson = json.encodeToString(config)
        val configPath = dir.resolve("config.json")
        logI(TAG, "Writing config to: $configPath")
        Files.writeString(configPath, configJson)

        logI(TAG, "Init completed. Agentic directory initialized at: $dir")
        echo("Initialized agentic at $dir")
        echo("Edit $configPath to configure your settings")
        echo("Edit docs in $docsDir before running 'agentic validate'")
    }
}

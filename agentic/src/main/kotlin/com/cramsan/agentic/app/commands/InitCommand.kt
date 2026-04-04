package com.cramsan.agentic.app.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.cramsan.agentic.core.AgenticConfig
import com.cramsan.agentic.core.VcsProviderConfig
import com.cramsan.agentic.input.DefaultScaffolder
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.nio.file.Files
import java.nio.file.Path

class InitCommand : CliktCommand(name = "init", help = "Scaffold input docs and write default config.json") {

    private val outputDir by option("--dir", help = "Output directory for docs").default(".agentic")

    override fun run() {
        val dir = Path.of(outputDir)
        Files.createDirectories(dir)
        val docsDir = dir.resolve("docs")
        Files.createDirectories(docsDir)

        val scaffolder = DefaultScaffolder()
        scaffolder.scaffold(docsDir)

        val config = AgenticConfig(
            agentPoolSize = 2,
            baseBranch = "main",
            docsDir = docsDir.toString(),
            vcsProvider = VcsProviderConfig.GitHub(owner = "your-org", repo = "your-repo"),
        )
        val json = Json { prettyPrint = true }
        val configJson = json.encodeToString(config)
        val configPath = dir.resolve("config.json")
        Files.writeString(configPath, configJson)

        echo("Initialized agentic at $dir")
        echo("Edit $configPath to configure your settings")
        echo("Edit docs in $docsDir before running 'agentic validate'")
    }
}

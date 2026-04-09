package com.cramsan.agentic.app.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.cramsan.agentic.app.agenticModule
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import java.nio.file.Path

class StatusCommand : CliktCommand(name = "status", help = "Show current task status") {

    private val configPath by option("--config", help = "Path to config.json").default(".agentic/config.json")

    override fun run() {
        val agenticDir = Path.of(configPath).parent ?: Path.of(".")
        val repoRoot = Path.of(".")

        val koin = startKoin {
            modules(agenticModule(agenticDir, repoRoot))
        }.koin

        try {
            val orchestrator = koin.get<com.cramsan.agentic.coordination.Orchestrator>()
            val statuses = runBlocking { orchestrator.status() }

            echo("%-20s %-12s %s".format("Task ID", "Status", "Title"))
            echo("-".repeat(60))
            statuses.entries
                .sortedBy { it.key.id }
                .forEach { (task, status) ->
                    echo("%-20s %-12s %s".format(task.id, status.name, task.title))
                }
        } finally {
            stopKoin()
        }
    }
}

package com.cramsan.agentic.app.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.cramsan.agentic.app.agenticModule
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import java.nio.file.Path
import java.util.Locale

/**
 * CLI command that prints a one-shot table of all task IDs, statuses, and titles. Does not
 * start the orchestrator polling loop — use `agentic run start` for that.
 *
 * Status is derived live by calling [com.cramsan.agentic.coordination.Orchestrator.status],
 * which makes VCS queries and reads the filesystem. The output reflects the state at the
 * moment the command runs, not a cached snapshot.
 */
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

            echo(String.format(Locale.ROOT, "%-20s %-12s %s", "Task ID", "Status", "Title"))
            echo("-".repeat(WIDTH))
            statuses.entries
                .sortedBy { it.key.id }
                .forEach { (task, status) ->
                    echo(String.format(Locale.ROOT, "%-20s %-12s %s", task.id, status.name, task.title))
                }
        } finally {
            stopKoin()
        }
    }
}

private const val WIDTH = 60


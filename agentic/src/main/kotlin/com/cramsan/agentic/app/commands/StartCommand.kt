package com.cramsan.agentic.app.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
import com.cramsan.agentic.app.agenticModule
import com.cramsan.agentic.coordination.OrchestratorConfig
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import java.nio.file.Path

class StartCommand : CliktCommand(name = "start", help = "Start the agentic orchestrator") {

    private val configPath by option("--config", help = "Path to config.json").default(".agentic/config.json")
    private val agentPoolSizeOverride by option("--agents", help = "Number of concurrent agents").int()
    private val dryRun by option("--dry-run", help = "Print task order without running agents").flag()

    override fun run() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))

        val agenticDir = Path.of(configPath).parent ?: Path.of(".")
        val repoRoot = Path.of(".")

        val koin = startKoin {
            modules(agenticModule(agenticDir, repoRoot))
        }.koin

        try {
            val agenticConfig = koin.get<com.cramsan.agentic.core.AgenticConfig>()
            val orchestrator = koin.get<com.cramsan.agentic.coordination.Orchestrator>()

            val orchConfig = OrchestratorConfig(
                agentPoolSize = agentPoolSizeOverride ?: agenticConfig.agentPoolSize,
                baseBranch = agenticConfig.baseBranch,
                claudeModel = agenticConfig.claudeModel,
            )

            if (dryRun) {
                val statuses = runBlocking { orchestrator.status() }
                echo("=== Task Status (dry run) ===")
                statuses.entries
                    .sortedBy { it.key.id }
                    .forEach { (task, status) ->
                        echo("  [${status.name}] ${task.id}: ${task.title}")
                    }
            } else {
                echo("Starting agentic orchestrator with ${orchConfig.agentPoolSize} agent(s)...")
                // TODO: When AiProviderConfig is implemented (see AMENDMENT_CLAUDE_CLI_PROVIDER.md),
                // validate here that if the configured provider does not support tool use (e.g., claude-cli),
                // the user is warned that agent task execution requires a tool-use-capable provider.
                // Reviewer and validation workloads work with any provider.
                runBlocking { orchestrator.run(orchConfig) }
                echo("Orchestrator run completed.")
            }
        } finally {
            stopKoin()
        }
    }
}

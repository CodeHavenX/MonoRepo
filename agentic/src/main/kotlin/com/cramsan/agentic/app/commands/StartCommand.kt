package com.cramsan.agentic.app.commands

import com.cramsan.agentic.ai.claude.ClaudeCliAiProvider
import com.cramsan.agentic.app.agenticModule
import com.cramsan.agentic.coordination.OrchestratorConfig
import com.cramsan.agentic.core.AiProviderConfig
import java.nio.file.Files
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import com.cramsan.framework.logging.logI
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import java.nio.file.Path

private const val TAG = "StartCommand"

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
            val taskListApproved = agenticDir.resolve("docs/task-list.approved")
            if (!Files.exists(taskListApproved)) {
                echo(
                    "ERROR: Planning phase is not complete. " +
                        "Run 'agentic plan generate' and approve each stage before starting.",
                    err = true,
                )
                throw com.github.ajalt.clikt.core.ProgramResult(1)
            }

            val agenticConfig = koin.get<com.cramsan.agentic.core.AgenticConfig>()
            val aiProvider = koin.get<com.cramsan.agentic.ai.AiProvider>()
            val orchestrator = koin.get<com.cramsan.agentic.coordination.Orchestrator>()

            // Startup validation: agent task execution requires tool-use support.
            // ClaudeCliAiProvider does not support tools — fail fast with a clear message.
            if (aiProvider is ClaudeCliAiProvider) {
                echo(
                    "ERROR: The configured AI provider (claude-cli) does not support tool use, " +
                        "which is required for agent task execution. " +
                        "Use 'claude-api' in config.json for start/resume, or use 'claude-cli' " +
                        "only for validate (which does not use tools).",
                    err = true,
                )
                throw com.github.ajalt.clikt.core.ProgramResult(1)
            }

            // Startup validation: verify the claude-api key is accessible.
            if (agenticConfig.aiProvider is AiProviderConfig.ClaudeApi) {
                val keyVar = (agenticConfig.aiProvider as AiProviderConfig.ClaudeApi).anthropicApiKeyEnvVar
                if (System.getenv(keyVar).isNullOrBlank()) {
                    echo("ERROR: Environment variable '$keyVar' is not set or empty.", err = true)
                    throw com.github.ajalt.clikt.core.ProgramResult(1)
                }
            }

            val orchConfig = OrchestratorConfig(
                agentPoolSize = agentPoolSizeOverride ?: agenticConfig.agentPoolSize,
                baseBranch = agenticConfig.baseBranch,
                claudeModel = agenticConfig.claudeModel,
            )

            logI(
                TAG,
                "Startup config summary — provider: ${agenticConfig.aiProvider::class.simpleName}, " +
                    "poolSize: ${orchConfig.agentPoolSize}, baseBranch: ${orchConfig.baseBranch}, " +
                    "model: ${orchConfig.claudeModel}",
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
                runBlocking { orchestrator.run(orchConfig) }
                logI(TAG, "Orchestrator finished.")
                echo("Orchestrator run completed.")
            }
        } finally {
            stopKoin()
        }
    }
}

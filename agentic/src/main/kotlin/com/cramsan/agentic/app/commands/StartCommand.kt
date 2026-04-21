package com.cramsan.agentic.app.commands

import com.cramsan.agentic.app.agenticModule
import com.cramsan.agentic.coordination.OrchestratorConfig
import com.cramsan.agentic.core.AiProviderConfig
import java.nio.file.Files
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

/**
 * CLI command that starts the autonomous agent orchestrator. Blocks until all tasks complete
 * (RunCompleted) or the run deadlocks (RunDeadlocked).
 *
 * **Prerequisites**: the planning phase must be complete (`.agentic-meta/stage.stage3.json`
 * must exist) and the AI provider must support tool use (ClaudeCli requires `fullAccess: true`).
 * Both are validated at startup before the orchestrator is started.
 *
 * **`--dry-run`**: derives and prints task statuses without launching any agents. Useful for
 * verifying configuration and dependency ordering before a real run.
 *
 * **`--agents`**: overrides [com.cramsan.agentic.core.AgenticConfig.agentPoolSize] for this
 * invocation without modifying `config.json`.
 */
class StartCommand : CliktCommand(name = "start", help = "Start the agentic orchestrator") {

    private val configPath by option("--config", help = "Path to config.json").default(".agentic/config.json")
    private val agentPoolSizeOverride by option("--agents", help = "Number of concurrent agents").int()
    private val dryRun by option("--dry-run", help = "Print task order without running agents").flag()

    override fun run() {
        val agenticDir = Path.of(configPath).parent ?: Path.of(".")
        val repoRoot = Path.of(".")

        val koin = startKoin {
            modules(agenticModule(agenticDir, repoRoot))
        }.koin

        try {
            validatePlanningComplete(agenticDir)

            val agenticConfig = koin.get<com.cramsan.agentic.core.AgenticConfig>()
            val orchestrator = koin.get<com.cramsan.agentic.coordination.Orchestrator>()

            validateAiProvider(agenticConfig)

            val orchConfig = OrchestratorConfig(
                agentPoolSize = agentPoolSizeOverride ?: agenticConfig.agentPoolSize,
                baseBranch = agenticConfig.baseBranch,
            )

            logI(
                TAG,
                "Startup config summary — provider: ${agenticConfig.aiProvider::class.simpleName}, " +
                    "model: ${agenticConfig.aiProvider.model}, " +
                    "poolSize: ${orchConfig.agentPoolSize}, baseBranch: ${orchConfig.baseBranch}",
            )

            if (dryRun) {
                printDryRunStatus(orchestrator)
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

    private fun validatePlanningComplete(agenticDir: Path) {
        val taskListApproved = agenticDir.resolve("docs/.agentic-meta/stage.stage3.json")
        if (!Files.exists(taskListApproved)) {
            echo(
                "ERROR: Planning phase is not complete. " +
                    "Run 'agentic plan generate' and approve each stage before starting.",
                err = true,
            )
            throw com.github.ajalt.clikt.core.ProgramResult(1)
        }
    }

    private fun validateAiProvider(agenticConfig: com.cramsan.agentic.core.AgenticConfig) {
        val provider = agenticConfig.aiProvider
        if (provider is AiProviderConfig.ClaudeCli && !provider.fullAccess) {
            echo(
                "ERROR: The configured AI provider (claude-cli) does not support tool use, " +
                    "which is required for agent task execution. " +
                    "Set \"fullAccess\": true in the claude-cli provider block in config.json " +
                    "to enable autonomous agent mode, or switch to 'claude-api'.",
                err = true,
            )
            throw com.github.ajalt.clikt.core.ProgramResult(1)
        }
        if (provider is AiProviderConfig.ClaudeApi) {
            val keyVar = provider.anthropicApiKeyEnvVar
            if (System.getenv(keyVar).isNullOrBlank()) {
                echo("ERROR: Environment variable '$keyVar' is not set or empty.", err = true)
                throw com.github.ajalt.clikt.core.ProgramResult(1)
            }
        }
    }

    private fun printDryRunStatus(orchestrator: com.cramsan.agentic.coordination.Orchestrator) {
        val statuses = runBlocking { orchestrator.status() }
        echo("=== Task Status (dry run) ===")
        statuses.entries
            .sortedBy { it.key.id }
            .forEach { (task, status) ->
                echo("  [${status.name}] ${task.id}: ${task.title}")
            }
    }
}

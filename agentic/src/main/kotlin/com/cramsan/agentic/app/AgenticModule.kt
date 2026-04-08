package com.cramsan.agentic.app

import com.cramsan.agentic.ai.AiProvider
import com.cramsan.agentic.ai.claude.ClaudeAiProvider
import com.cramsan.agentic.ai.claude.ClaudeCliAiProvider
import com.cramsan.agentic.claude.DefaultAgentSession
import com.cramsan.agentic.coordination.DefaultDependencyGraph
import com.cramsan.agentic.coordination.DefaultOrchestrator
import com.cramsan.agentic.coordination.DefaultStateDeriver
import com.cramsan.agentic.coordination.DependencyGraph
import com.cramsan.agentic.coordination.FileSystemTaskStore
import com.cramsan.agentic.coordination.Orchestrator
import com.cramsan.agentic.coordination.StateDeriver
import com.cramsan.agentic.coordination.TaskStore
import com.cramsan.agentic.core.AgenticConfig
import com.cramsan.agentic.core.AiProviderConfig
import com.cramsan.agentic.core.VcsProviderConfig
import com.cramsan.agentic.execution.AgentRunner
import com.cramsan.agentic.execution.AgentSession
import com.cramsan.agentic.execution.DefaultAgentRunner
import com.cramsan.agentic.execution.DefaultWorktreeManager
import com.cramsan.agentic.execution.WorktreeManager
import com.cramsan.agentic.input.DefaultScaffolder
import com.cramsan.agentic.input.DefaultValidationService
import com.cramsan.agentic.input.DefaultWorkflowService
import com.cramsan.agentic.input.DocumentStore
import com.cramsan.agentic.input.FileSystemDocumentStore
import com.cramsan.agentic.input.Scaffolder
import com.cramsan.agentic.input.ValidationService
import com.cramsan.agentic.input.WorkflowService
import com.cramsan.agentic.notification.Notifier
import com.cramsan.agentic.notification.vcs.VcsCommentNotifier
import com.cramsan.agentic.reviewer.FileSystemReviewerLoader
import com.cramsan.agentic.reviewer.ReviewerAgent
import com.cramsan.agentic.reviewer.ReviewerLoader
import com.cramsan.agentic.reviewer.claude.ClaudeReviewerAgent
import com.cramsan.agentic.vcs.VcsProvider
import com.cramsan.agentic.vcs.github.GitHubVcsProvider
import com.cramsan.agentic.vcs.github.ShellRunner
import com.cramsan.framework.logging.logD
import com.cramsan.framework.logging.logI
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import java.nio.file.Path

private const val TAG = "AgenticModule"

fun agenticModule(
    agenticDir: Path,
    repoRoot: Path,
    configPath: Path = agenticDir.resolve("config.json"),
) = module {

    single<Json> {
        Json { ignoreUnknownKeys = true; isLenient = true }
    }

    single<ShellRunner> { ShellRunner() }

    single<HttpClient> {
        HttpClient(CIO) {
            install(ContentNegotiation) { json(get()) }
        }
    }

    single<AgenticConfig> {
        val json = get<Json>()
        json.decodeFromString(java.nio.file.Files.readString(configPath))
    }

    single<VcsProvider> {
        val config = get<AgenticConfig>()
        when (val vcs = config.vcsProvider) {
            is VcsProviderConfig.GitHub -> {
                logI(TAG, "Configuring VCS provider: GitHub (owner=${vcs.owner}, repo=${vcs.repo})")
                GitHubVcsProvider(vcs.owner, vcs.repo, get(), get())
            }
        }
    }

    single<WorktreeManager> {
        val config = get<AgenticConfig>()
        DefaultWorktreeManager(repoRoot, agenticDir, config.baseBranch, get())
    }

    single<TaskStore> {
        FileSystemTaskStore(agenticDir.resolve("docs/task-list.md"))
    }

    single<DocumentStore> {
        FileSystemDocumentStore(agenticDir.resolve("docs"), get())
    }

    single<DependencyGraph> {
        DefaultDependencyGraph(get<TaskStore>().getAll())
    }

    single<StateDeriver> {
        DefaultStateDeriver(get(), get(), agenticDir)
    }

    single<Notifier> {
        VcsCommentNotifier(get())
    }

    single<ReviewerLoader> {
        FileSystemReviewerLoader(agenticDir.resolve("docs/reviewers"))
    }

    single<AiProvider> {
        val config = get<AgenticConfig>()
        when (val aiConfig = config.aiProvider) {
            is AiProviderConfig.ClaudeApi -> {
                logI(TAG, "Configuring AI provider: ClaudeApi (apiKeyEnvVar=${aiConfig.anthropicApiKeyEnvVar})")
                val apiKey = System.getenv(aiConfig.anthropicApiKeyEnvVar)
                    ?: error("API key env var '${aiConfig.anthropicApiKeyEnvVar}' is not set")
                logD(TAG, "ClaudeApi API key resolved from env var: ${aiConfig.anthropicApiKeyEnvVar}")
                ClaudeAiProvider(get(), apiKey, get())
            }
            is AiProviderConfig.ClaudeCli -> {
                logI(TAG, "Configuring AI provider: ClaudeCli (cliPath=${aiConfig.cliPath})")
                ClaudeCliAiProvider(get(), aiConfig.cliPath)
            }
        }
    }

    single<ReviewerAgent> {
        val config = get<AgenticConfig>()
        ClaudeReviewerAgent(get(), config.claudeModel)
    }

    single<AgentSession> {
        val config = get<AgenticConfig>()
        DefaultAgentSession(get(), get(), get(), config.claudeModel, config.baseBranch, get())
    }

    single<AgentRunner> {
        DefaultAgentRunner(get(), get(), listOf(get()), get(), get(), agenticDir)
    }

    single<Orchestrator> {
        DefaultOrchestrator(get(), get(), get(), get(), get(), get())
    }

    single<Scaffolder> { DefaultScaffolder() }

    single<ValidationService> {
        val config = get<AgenticConfig>()
        DefaultValidationService(get(), get(), config.claudeModel, listOf(get()), get(), get(), agenticDir.resolve("docs"))
    }

    single<WorkflowService> {
        val config = get<AgenticConfig>()
        DefaultWorkflowService(get(), get(), config.claudeModel, agenticDir.resolve("docs"), config.workflow)
    }
}

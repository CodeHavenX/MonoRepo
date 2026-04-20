package com.cramsan.agentic.app

import com.cramsan.agentic.ai.AiProvider
import com.cramsan.agentic.ai.RetryingAiProvider
import com.cramsan.agentic.ai.claude.ClaudeAiProvider
import com.cramsan.agentic.ai.claude.ClaudeCliAiProvider
import com.cramsan.agentic.ai.fake.FakeAiProvider
import com.cramsan.agentic.claude.DefaultAgentSession
import com.cramsan.agentic.coordination.DefaultDependencyGraph
import com.cramsan.agentic.coordination.DefaultOrchestrator
import com.cramsan.agentic.coordination.DefaultStateDeriver
import com.cramsan.agentic.coordination.DependencyGraph
import com.cramsan.agentic.coordination.DocumentTaskListProvider
import com.cramsan.agentic.coordination.Orchestrator
import com.cramsan.agentic.coordination.StateDeriver
import com.cramsan.agentic.coordination.TaskListProvider
import com.cramsan.agentic.coordination.fake.FakeTaskListProvider
import com.cramsan.agentic.core.AgenticConfig
import com.cramsan.agentic.core.AiProviderConfig
import com.cramsan.agentic.core.PlanningConfig
import com.cramsan.agentic.core.TaskListProviderConfig
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
import com.cramsan.agentic.reviewer.ConfigurableReviewerLoader
import com.cramsan.agentic.reviewer.ReviewerAgent
import com.cramsan.agentic.reviewer.ReviewerLoader
import com.cramsan.agentic.reviewer.claude.ClaudeReviewerAgent
import com.cramsan.agentic.vcs.VcsProvider
import com.cramsan.agentic.vcs.github.GitHubVcsProvider
import com.cramsan.agentic.vcs.github.ShellRunner
import com.cramsan.agentic.vcs.local.LocalVcsProvider
import com.cramsan.framework.logging.logD
import com.cramsan.framework.logging.logI
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import java.nio.file.Path

private const val TAG = "AgenticModule"

fun agenticModule(
    agenticDir: Path,
    repoRoot: Path,
    configPath: Path = agenticDir.resolve("config.json"),
    planningPath: Path = agenticDir.resolve("planning.json"),
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

    single<PlanningConfig> {
        val json = get<Json>()
        json.decodeFromString(java.nio.file.Files.readString(planningPath))
    }

    single<VcsProvider> {
        val config = get<AgenticConfig>()
        when (val vcs = config.vcsProvider) {
            is VcsProviderConfig.GitHub -> {
                logI(TAG, "Configuring VCS provider: GitHub (owner=${vcs.owner}, repo=${vcs.repo})")
                GitHubVcsProvider(vcs.owner, vcs.repo, get(), get())
            }
            is VcsProviderConfig.Local -> {
                logI(TAG, "Configuring VCS provider: Local (stateFile=${vcs.stateFile}, autoMerge=${vcs.autoMerge})")
                LocalVcsProvider(
                    stateFile = agenticDir.resolve(vcs.stateFile),
                    autoMerge = vcs.autoMerge,
                    repoRoot = repoRoot,
                    shell = get(),
                    json = get(),
                )
            }
        }
    }

    single<WorktreeManager> {
        val config = get<AgenticConfig>()
        DefaultWorktreeManager(repoRoot, agenticDir, config.baseBranch, get())
    }

    single<TaskListProvider> {
        val config = get<AgenticConfig>()
        when (val tlConfig = config.taskListProvider) {
            is TaskListProviderConfig.Document -> {
                logI(TAG, "Configuring TaskListProvider: Document (documentPath=${tlConfig.documentPath})")
                DocumentTaskListProvider(
                    documentPath = agenticDir.resolve(tlConfig.documentPath),
                    tasksDir = agenticDir.resolve(tlConfig.tasksOutputDir),
                    json = get(),
                )
            }
            is TaskListProviderConfig.Fake -> {
                logI(TAG, "Configuring TaskListProvider: Fake")
                FakeTaskListProvider()
            }
        }
    }

    single<DocumentStore> {
        val planningConfig = get<PlanningConfig>()
        FileSystemDocumentStore(agenticDir.resolve("docs"), get(), planningConfig.inputDocuments)
    }

    single<DependencyGraph> {
        val tasks = runBlocking { get<TaskListProvider>().provide() }
        DefaultDependencyGraph(tasks)
    }

    single<StateDeriver> {
        DefaultStateDeriver(get(), get(), agenticDir)
    }

    single<Notifier> {
        VcsCommentNotifier(get())
    }

    single<ReviewerLoader> {
        val planningConfig = get<PlanningConfig>()
        ConfigurableReviewerLoader(planningConfig.reviewers, agenticDir.resolve("docs"))
    }

    single<AiProvider> {
        val config = get<AgenticConfig>()
        when (val aiConfig = config.aiProvider) {
            is AiProviderConfig.ClaudeApi -> {
                logI(TAG, "Configuring AI provider: ClaudeApi (model=${aiConfig.model}, apiKeyEnvVar=${aiConfig.anthropicApiKeyEnvVar})")
                val apiKey = System.getenv(aiConfig.anthropicApiKeyEnvVar)
                    ?: error("API key env var '${aiConfig.anthropicApiKeyEnvVar}' is not set")
                logD(TAG, "ClaudeApi API key resolved from env var: ${aiConfig.anthropicApiKeyEnvVar}")
                RetryingAiProvider(ClaudeAiProvider(get(), apiKey, get(), aiConfig.model))
            }
            is AiProviderConfig.ClaudeCli -> {
                logI(TAG, "Configuring AI provider: ClaudeCli (model=${aiConfig.model}, cliPath=${aiConfig.cliPath}, fullAccess=${aiConfig.fullAccess})")
                RetryingAiProvider(
                    ClaudeCliAiProvider(
                        shell = get(),
                        cliPath = aiConfig.cliPath,
                        model = aiConfig.model,
                        fullAccess = aiConfig.fullAccess,
                        json = if (aiConfig.fullAccess) get() else null,
                    )
                )
            }
            is AiProviderConfig.Fake -> {
                logI(TAG, "Configuring AI provider: Fake (model=${aiConfig.model}, mode=${aiConfig.mode})")
                FakeAiProvider(
                    model = aiConfig.model,
                    mode = aiConfig.mode,
                    delayMs = aiConfig.delayMs,
                    autoCompleteAfterTurns = aiConfig.autoCompleteAfterTurns,
                    defaultTextResponse = aiConfig.defaultTextResponse,
                )
            }
        }
    }

    single<ReviewerAgent> {
        ClaudeReviewerAgent(get())
    }

    single<AgentSession> {
        val config = get<AgenticConfig>()
        DefaultAgentSession(get(), get(), get(), config.baseBranch, get())
    }

    single<AgentRunner> {
        DefaultAgentRunner(get(), get(), listOf(get()), get(), get(), agenticDir)
    }

    single<Orchestrator> {
        DefaultOrchestrator(get(), get(), get(), get(), get(), get())
    }

    single<Scaffolder> {
        val planningConfig = get<PlanningConfig>()
        DefaultScaffolder(
            planningConfig.inputDocuments,
            planningConfig.reviewers,
            planningConfig.workflow.stages,
        )
    }

    single<ValidationService> {
        DefaultValidationService(get(), get(), listOf(get()), get(), get(), agenticDir.resolve("docs"))
    }

    single<WorkflowService> {
        val planningConfig = get<PlanningConfig>()
        DefaultWorkflowService(get(), get(), agenticDir.resolve("docs"), get(), planningConfig.workflow)
    }
}

package com.cramsan.agentic.app

import com.cramsan.agentic.claude.ClaudeClient
import com.cramsan.agentic.claude.DefaultAgentSession
import com.cramsan.agentic.claude.KtorClaudeClient
import com.cramsan.agentic.coordination.DefaultDependencyGraph
import com.cramsan.agentic.coordination.DefaultOrchestrator
import com.cramsan.agentic.coordination.DefaultStateDeriver
import com.cramsan.agentic.coordination.DependencyGraph
import com.cramsan.agentic.coordination.FileSystemTaskStore
import com.cramsan.agentic.coordination.Orchestrator
import com.cramsan.agentic.coordination.StateDeriver
import com.cramsan.agentic.coordination.TaskStore
import com.cramsan.agentic.core.AgenticConfig
import com.cramsan.agentic.core.VcsProviderConfig
import com.cramsan.agentic.execution.AgentRunner
import com.cramsan.agentic.execution.AgentSession
import com.cramsan.agentic.execution.DefaultAgentRunner
import com.cramsan.agentic.execution.DefaultWorktreeManager
import com.cramsan.agentic.execution.WorktreeManager
import com.cramsan.agentic.input.DefaultScaffolder
import com.cramsan.agentic.input.DefaultValidationService
import com.cramsan.agentic.input.DocumentStore
import com.cramsan.agentic.input.FileSystemDocumentStore
import com.cramsan.agentic.input.Scaffolder
import com.cramsan.agentic.input.ValidationService
import com.cramsan.agentic.notification.Notifier
import com.cramsan.agentic.notification.vcs.VcsCommentNotifier
import com.cramsan.agentic.reviewer.FileSystemReviewerLoader
import com.cramsan.agentic.reviewer.ReviewerAgent
import com.cramsan.agentic.reviewer.ReviewerLoader
import com.cramsan.agentic.reviewer.claude.ClaudeReviewerAgent
import com.cramsan.agentic.vcs.VcsProvider
import com.cramsan.agentic.vcs.github.GitHubVcsProvider
import com.cramsan.agentic.vcs.github.ShellRunner
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import java.nio.file.Path

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
            is VcsProviderConfig.GitHub -> GitHubVcsProvider(vcs.owner, vcs.repo, get(), get())
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

    single<ReviewerAgent> {
        val config = get<AgenticConfig>()
        ClaudeReviewerAgent(get(), config.claudeModel)
    }

    single<ClaudeClient> {
        val config = get<AgenticConfig>()
        val apiKey = System.getenv(config.anthropicApiKeyEnvVar)
            ?: error("API key env var '${config.anthropicApiKeyEnvVar}' is not set")
        KtorClaudeClient(get(), apiKey, get())
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
}

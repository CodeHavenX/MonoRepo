# Agentic — Technical Design

> Status: Draft
> Depends on: ARCHITECTURE.md

---

## 1. Overview

This document translates the architecture defined in `ARCHITECTURE.md` into concrete implementation decisions: technology stack, module layout, data models, component interfaces, persistence schema, external integrations, and CLI design.

Agentic is a **JVM CLI application** written in Kotlin. It is self-contained — it does not expose a server or API. Operators run it directly in a terminal or in CI.

---

## 2. Technology Stack

| Concern | Choice | Reason |
|---------|--------|--------|
| Language | Kotlin (JVM) | Monorepo is already Kotlin; JVM is the right target for a CLI tool |
| CLI framework | [Clikt](https://github.com/ajalt/clikt) | Idiomatic Kotlin CLI library; composable subcommands |
| Async runtime | Kotlin Coroutines | Concurrent agent execution without thread-per-agent overhead |
| Serialization | `kotlinx.serialization` (JSON) | Already used in the monorepo |
| HTTP client | Ktor Client | Already used in the monorepo; needed for Claude API |
| VCS provider | Provider interface + GitHub impl (`gh` CLI) | Abstraction keeps orchestrator provider-agnostic; `gh` handles auth for free |
| AI agents | Anthropic API (HTTP via Ktor) | Direct control over prompts, tools, and context resets |
| Logging | `cramsan-framework-logging` | Existing monorepo framework |

---

## 3. Module Layout

The system lives under `agentic/` as a single JVM Gradle module. Internal packages map to the three architecture layers:

```
agentic/
  build.gradle.kts
  src/
    main/kotlin/com/cramsan/agentic/
      core/               # Shared models, interfaces, Result types
      input/              # Scaffolding, validation, document store
      coordination/       # Orchestrator, task queue, dependency graph
      execution/          # Agent runner, worktree manager
      vcs/                # VcsProvider interface + data models
        github/           # GitHub implementation (gh CLI wrappers)
        fake/             # In-memory implementation for tests
      notification/       # Notifier interface + AgenticEvent models
        vcs/              # VCS comment implementation
        fake/             # No-op implementation for tests
      claude/             # Claude API client and agent prompts
      app/                # CLI entry point, command definitions
    test/kotlin/com/cramsan/agentic/
      ...                 # Unit tests per package
    integTest/kotlin/com/cramsan/agentic/
      ...                 # Integration tests (real filesystem, mocked APIs)
```

A single module is intentional — the layers are tightly coupled at runtime and splitting them into submodules adds Gradle overhead with no benefit at this scale.

---

## 4. Data Models

All models live in `core/`. They are `@Serializable` data classes persisted as JSON.

### 4.1 Documents

```kotlin
@Serializable
data class AgenticDocument(
    val id: String,
    val type: DocumentType,
    val relativePath: String,       // relative to .agentic/docs/
    val status: DocumentStatus,
    val lastModifiedEpochMs: Long,
)

@Serializable
enum class DocumentType {
    GOALS_SCOPE,
    ARCHITECTURE_DESIGN,
    STANDARDS,
    TASK_LIST,
}

@Serializable
enum class DocumentStatus {
    UNREVIEWED,
    IN_REVIEW,
    NEEDS_REVISION,
    VALIDATED,
}

@Serializable
data class ValidationIssue(
    val id: String,
    val documentId: String,
    val description: String,
    val severity: IssueSeverity,
    val status: IssueStatus,
)

@Serializable
enum class IssueSeverity { BLOCKING, ADVISORY }

@Serializable
enum class IssueStatus { OPEN, ADDRESSED, DISMISSED }

@Serializable
data class ValidationReport(
    val runId: String,
    val timestampEpochMs: Long,
    val issues: List<ValidationIssue>,
)
```

### 4.2 Tasks

```kotlin
@Serializable
data class Task(
    val id: String,
    val title: String,
    val description: String,
    val dependencies: List<String>,         // task IDs
    val status: TaskStatus,
    val worktreePath: String? = null,       // .agentic/worktrees/{task-id}/
    val prUrl: String? = null,
    val prId: String? = null,               // provider-assigned identifier
    val assignedAtEpochMs: Long? = null,
    val completedAtEpochMs: Long? = null,
    val failureReason: String? = null,
    val timeoutSeconds: Long = 3600L,
)

@Serializable
enum class TaskStatus {
    PENDING,
    IN_PROGRESS,
    IN_REVIEW,
    DONE,
    BLOCKED,
    FAILED,
}
```

### 4.3 Amendments

```kotlin
@Serializable
data class Amendment(
    val id: String,
    val proposedByTaskId: String,
    val targetDocumentId: String,
    val description: String,
    val isCritical: Boolean,
    val prUrl: String? = null,
    val prId: String? = null,               // provider-assigned identifier
    val status: AmendmentStatus,
)

@Serializable
enum class AmendmentStatus { PROPOSED, OPEN, MERGED, REJECTED }
```

### 4.4 Orchestrator State

```kotlin
@Serializable
data class OrchestratorState(
    val runId: String,
    val startedAtEpochMs: Long,
    val agentPoolSize: Int,
    val tasks: List<Task>,
    val amendments: List<Amendment>,
    val activeAssignments: List<AgentAssignment>,
)

@Serializable
data class AgentAssignment(
    val agentId: String,
    val taskId: String,
    val assignedAtEpochMs: Long,
)
```

---

## 5. Component Interfaces

### 5.1 Input Layer

```kotlin
/** Generates the starter document set under .agentic/docs/. */
interface Scaffolder {
    fun scaffold(outputDir: Path)
}

/** Persistent access to the four input documents. */
interface DocumentStore {
    fun getAll(): List<AgenticDocument>
    fun get(id: String): AgenticDocument
    fun updateStatus(id: String, status: DocumentStatus)
    /** Called when any document file changes on disk. Resets all to UNREVIEWED. */
    fun onDocumentChanged()
    /** Returns true when all documents are VALIDATED. */
    fun allValidated(): Boolean
}

/** Runs a validation pass via Claude. */
interface ValidationService {
    /** Reviews a single document and returns any issues found. */
    suspend fun reviewDocument(document: AgenticDocument): List<ValidationIssue>
    /**
     * Runs a full validation pass across all documents. Updates DocumentStore statuses
     * and returns the report. The caller (CLI) is responsible for printing results and
     * exiting. The human re-runs `agentic validate` after editing documents.
     */
    suspend fun runValidationPass(): ValidationReport
}
```

### 5.2 Coordination Layer

```kotlin
/** Parses tasks from the validated TASK_LIST document. */
interface TaskParser {
    fun parse(taskListPath: Path): List<Task>
}

/** Persistent access to tasks. State is written to disk on every mutation. */
interface TaskStore {
    fun getAll(): List<Task>
    fun get(id: String): Task
    fun update(task: Task)
    fun updateStatus(id: String, status: TaskStatus)
}

/** Immutable view of the dependency graph. Rebuilt when tasks change. */
interface DependencyGraph {
    /** Tasks whose dependencies are all DONE. */
    fun readyTasks(): List<Task>
    /** Tasks that are blocked by at least one non-DONE dependency. */
    fun blockedTasks(): List<Task>
    /** Number of tasks (direct + transitive) that depend on this task. */
    fun downstreamCount(taskId: String): Int
    /** True if no task is PENDING or IN_PROGRESS and some tasks are still BLOCKED or FAILED. */
    fun isDeadlocked(): Boolean
}

/** Top-level coordinator: owns the run loop and agent pool. */
interface Orchestrator {
    /** Start a fresh run. Blocks until done, deadlocked, or interrupted. */
    suspend fun start(config: OrchestratorConfig)
    /** Resume an interrupted run. Reconstructs state from PRs + worktrees. */
    suspend fun resume(config: OrchestratorConfig)
    /** Current snapshot of the run. */
    fun currentState(): OrchestratorState
}

data class OrchestratorConfig(
    val agentPoolSize: Int,
    val defaultTaskTimeoutSeconds: Long,
    val baseBranch: String,
    val claudeModel: String,
    val vcsProvider: VcsProviderConfig,
)

sealed class VcsProviderConfig {
    data class GitHub(val owner: String, val repo: String) : VcsProviderConfig()
    // Future: data class GitLab(...) : VcsProviderConfig()
}
```

### 5.3 Execution Layer

```kotlin
/**
 * Manages git worktrees under .agentic/worktrees/.
 *
 * Worktrees are created lazily — [create] is called only when an agent is assigned
 * a task, not at run start. [delete] is called immediately when a task reaches
 * [TaskStatus.DONE] or [TaskStatus.FAILED].
 */
interface WorktreeManager {
    /** Creates a new worktree for the given task. Throws if one already exists. */
    fun create(taskId: String): Worktree
    /** Returns the existing worktree for a task, or null if it has not been created yet. */
    fun get(taskId: String): Worktree?
    /** Lists all worktrees currently on disk. Used during resume to reconstruct state. */
    fun listAll(): List<Worktree>
    /** Deletes the worktree. Called on task DONE or FAILED. */
    fun delete(taskId: String)
}

data class Worktree(
    val taskId: String,
    val path: Path,
    val branchName: String,
)

/** Runs an agent against a task. Returns when the agent reaches a terminal state. */
interface AgentRunner {
    suspend fun run(task: Task, worktree: Worktree): AgentResult
}

sealed class AgentResult {
    /** Agent opened a PR and is awaiting review. */
    data class PrOpened(val prId: String, val prUrl: String) : AgentResult()
    /** Agent could not complete the task. */
    data class Failed(val reason: String) : AgentResult()
    /** Agent proposed a critical amendment and is waiting for it to be merged. */
    data class AwaitingAmendment(val amendmentPrId: String) : AgentResult()
}
```

### 5.4 VCS Provider

The `VcsProvider` interface is the single point of contact for all pull request operations. The orchestrator, agents, and coordination layer all depend on this interface — no component is aware of which provider is active.

```kotlin
/** Provider-agnostic pull request operations. All calls are suspending (IO dispatcher). */
interface VcsProvider {
    suspend fun createPullRequest(
        sourceBranch: String,
        targetBranch: String,
        title: String,
        body: String,
        labels: List<String> = emptyList(),
    ): PullRequest

    suspend fun listOpenPullRequests(labels: List<String> = emptyList()): List<PullRequest>
    suspend fun listMergedPullRequests(labels: List<String> = emptyList()): List<PullRequest>
    suspend fun getPullRequestComments(prId: String): List<PullRequestComment>
    suspend fun addPullRequestComment(prId: String, body: String)
    suspend fun isPullRequestMerged(prId: String): Boolean
    suspend fun pullRequestHasRequestedChanges(prId: String): Boolean
}

@Serializable
data class PullRequest(
    val id: String,             // provider-assigned; opaque to the rest of the system
    val url: String,
    val title: String,
    val state: PullRequestState,
    val sourceBranch: String,
    val targetBranch: String,
    val labels: List<String>,
)

@Serializable
enum class PullRequestState { OPEN, CLOSED, MERGED }

@Serializable
data class PullRequestComment(
    val author: String,
    val body: String,
    val createdAtEpochMs: Long,
)
```

**Bundled implementations:**

| Class | Package | Description |
|-------|---------|-------------|
| `GitHubVcsProvider` | `vcs/github/` | Wraps `gh` CLI shell calls |
| `FakeVcsProvider` | `vcs/fake/` | In-memory; used in unit and integration tests |

The active implementation is selected at startup based on the `vcsProvider` field in `config.json` and injected via DI. No conditional logic exists outside the DI wiring.

### 5.5 Notification

```kotlin
/** Delivers human-facing notifications for significant orchestrator events. */
interface Notifier {
    suspend fun notify(event: AgenticEvent)
}

sealed class AgenticEvent {
    /** An agent could not complete a task. */
    data class TaskFailed(
        val task: Task,
        val reason: String,
    ) : AgenticEvent()

    /** No task can make progress; human intervention is required. */
    data class RunDeadlocked(
        val blockedTasks: List<Task>,
        val failedTasks: List<Task>,
    ) : AgenticEvent()

    /** All tasks are done; the run is complete. */
    data class RunCompleted(
        val completedTasks: List<Task>,
    ) : AgenticEvent()
}
```

**Bundled implementations:**

| Class | Package | Description |
|-------|---------|-------------|
| `VcsCommentNotifier` | `notification/vcs/` | Posts a comment on the task's PR via `VcsProvider` |
| `FakeNotifier` | `notification/fake/` | Records events in memory; used in tests |

The active implementation is injected via DI. Multiple notifiers can be composed with a `CompositeNotifier` if more than one channel is needed in the future.

### 5.6 Claude Integration

```kotlin
/** Sends a conversation to the Claude API. */
interface ClaudeClient {
    suspend fun chat(
        model: String,
        systemPrompt: String,
        messages: List<ClaudeMessage>,
        tools: List<ClaudeTool>,
    ): ClaudeResponse
}

/** High-level agent that drives a task to completion using Claude. */
interface AgentSession {
    /**
     * Runs the full agent lifecycle for one task. Returns when the agent
     * either opens a PR, fails, or pauses for a critical amendment.
     */
    suspend fun execute(task: Task, worktree: Worktree): AgentResult
}
```

---

## 6. Persistence

All runtime state lives under `.agentic/` in the repository root.

```
.agentic/
  config.json               # OrchestratorConfig (written by `agentic init`)
  state.json                # OrchestratorState snapshot (updated on every mutation)
  docs/
    goals-scope.md
    architecture-design.md
    standards.md
    task-list.md
    validation-report.md    # Generated; updated after each validation pass
  worktrees/
    {task-id}/              # Git worktree — created on agent assignment, deleted on DONE or FAILED
```

**State durability:** `state.json` is written atomically (write to temp file, rename) after every status change. The orchestrator never holds exclusive state in memory — it can be killed at any point and resume cleanly.

**Worktree naming:** Branch names follow the pattern `agentic/{task-id}`. The worktree path `.agentic/worktrees/{task-id}` is derived from the task ID alone, making the mapping implicit and filesystem-recoverable.

**`.agentic/` in `.gitignore`:** The `.agentic/` directory is excluded from version control. Docs authored by the human live in a human-managed location (e.g., `agentic-docs/`) and are referenced by path in `config.json`.

---

## 7. External Integrations

### 7.1 VCS Provider

The system interacts with the remote repository exclusively through the `VcsProvider` interface (§5.4). The active provider is chosen by the `vcsProvider` field in `config.json`.

#### GitHub Implementation (`GitHubVcsProvider`)

All calls delegate to the `gh` CLI, which handles authentication externally.

Key commands:

```bash
# Create a PR
gh pr create --title "..." --body "..." --base main --head agentic/{task-id} --label "agentic"

# List open PRs for this run
gh pr list --label "agentic" --json number,url,title,state,headRefName

# Get PR review state
gh pr view {id} --json state,reviewDecision,reviews

# Get PR comments
gh pr view {id} --json comments

# Poll for merge
gh pr view {id} --json mergedAt
```

`GitHubVcsProvider` maps `PullRequest.id` to the GitHub PR number (as a string). This field is opaque everywhere outside `vcs/github/`.

PR labels (`agentic-code`, `agentic-document`) distinguish code PRs from document amendment PRs.

#### Fake Implementation (`FakeVcsProvider`)

`FakeVcsProvider` stores pull requests and comments in memory. Tests drive it by directly manipulating its state (e.g., marking a PR merged, appending review comments) without any network or process calls. It is the only provider used in unit and integration tests.

### 7.2 Notifications (`VcsCommentNotifier`)

`VcsCommentNotifier` is the default `Notifier` implementation. It posts a formatted comment on the relevant PR via `VcsProvider.addPullRequestComment`.

**Comment targets by event:**

| Event | Target |
|-------|--------|
| `TaskFailed` | The task's own PR (`task.prId`); if no PR exists yet, the event is console-only |
| `RunDeadlocked` | The most recently opened PR still in `in_review`; if none, console-only |
| `RunCompleted` | No PR comment — console output only |

**Comment format** — each comment is prefixed with a consistent header so humans can distinguish agentic notifications from agent code review comments:

```
<!-- agentic-notification -->
**Agentic — {Event Title}**

{Event body}
```

The `<!-- agentic-notification -->` HTML comment acts as a stable marker so the system can detect whether a notification has already been posted (avoiding duplicates on resume).

### 7.3 Claude API

Agents use the Anthropic Messages API via direct HTTP (Ktor client). No third-party SDK.

**Endpoint:** `POST https://api.anthropic.com/v1/messages`

**Model:** Configurable; default `claude-opus-4-6`.

**Agent tools** (declared in each API request):

| Tool | Description |
|------|-------------|
| `read_file` | Read a file at a given path |
| `write_file` | Write content to a file |
| `delete_file` | Delete a file |
| `run_command` | Run a shell command (tests, build, linters) in the worktree |
| `list_files` | List files matching a glob pattern |
| `task_complete` | Signal that work is done; triggers PR creation |
| `task_failed` | Signal that the task cannot be completed |
| `propose_amendment` | Propose a change to an input document |
| `split_task` | Open a Code PR for current work + propose a Document PR for remaining work |

**Context reset strategy** — the agent session rebuilds context from durable artifacts at each milestone, discarding raw conversation history:

| Milestone | System prompt content |
|-----------|-----------------------|
| Task start | Input docs (full text) + task description |
| PR opened (awaiting review) | Git diff of all changes + PR description |
| Changes requested | Previous git diff + PR review comments |
| Resume from worktree | Current git diff against base branch |

---

## 8. CLI Interface

Entry point: `com.cramsan.agentic.app.Main`

```
agentic <command> [options]

Commands:
  init                  Scaffold input docs and write default config.json
  validate              Run a validation pass, print findings, and exit (non-zero if blocking issues remain)
  start                 Start a fresh orchestrator run
  resume                Resume an interrupted run from current state
  status                Print current run state (tasks, assignments, amendments)
  task list             List all tasks with current status
  task show <id>        Show full detail for a task (status, PR, failure reason)
  task retry <id>       Re-queue a FAILED task for a new agent attempt
  task unblock <id>     Manually clear a BLOCKED state (after human fix)
```

**Global flags:**

```
  --config <path>       Path to config.json (default: .agentic/config.json)
  --log-level <level>   debug | info | warn | error (default: info)
```

**`start` / `resume` flags:**

```
  --agents <N>          Override agent pool size from config
  --dry-run             Plan tasks and print assignment order; do not run agents
```

---

## 9. Configuration

`config.json` schema:

```json
{
  "agentPoolSize": 3,
  "defaultTaskTimeoutSeconds": 3600,
  "baseBranch": "main",
  "claudeModel": "claude-opus-4-6",
  "docsDir": "agentic-docs",
  "anthropicApiKeyEnvVar": "ANTHROPIC_API_KEY",
  "vcsProvider": {
    "type": "github",
    "owner": "cramsan",
    "repo": "MonoRepo"
  }
}
```

The `vcsProvider.type` field determines which `VcsProvider` implementation is instantiated. Currently `"github"` is the only valid value. The provider-specific fields (`owner`, `repo`) are only read by the corresponding implementation.

The Anthropic API key is read from the environment variable named in `anthropicApiKeyEnvVar`. It is never written to disk.

---

## 10. Concurrency Model

```
main coroutine (Orchestrator)
    │
    ├── Dispatcher.IO: VCS provider polling loop (PR status checks, every 60s)
    │
    ├── Agent coroutine 1  ─── Dispatcher.IO: shell commands inside agent
    ├── Agent coroutine 2
    └── Agent coroutine N   (pool size bounded by config)
```

- The orchestrator loop runs on the main dispatcher and drives the assignment loop.
- Each agent runs in its own coroutine (`launch`). The pool is bounded by a `Semaphore(agentPoolSize)`.
- VCS provider polling is a separate periodic coroutine on `Dispatcher.IO`.
- All shared state (`TaskStore`, `OrchestratorState`) is protected by a `Mutex`. Disk writes happen inside the lock.
- A stalled-agent watchdog runs as a separate coroutine, checking `assignedAtEpochMs` against `timeoutSeconds` every 60 seconds.

---

## 11. Error Handling

| Failure | Handling |
|---------|---------|
| Agent signals `task_failed` | Task → FAILED; dependents → BLOCKED; `Notifier.notify(TaskFailed)` called |
| Agent exceeds timeout | Orchestrator kills the coroutine; task → FAILED; `Notifier.notify(TaskFailed)` called |
| VCS provider call fails | Retry up to 3 times with exponential backoff; if still failing, surface as task FAILED |
| Claude API returns error | Retry transient errors (5xx, 429); surface persistent errors as task FAILED |
| Deadlock detected | Orchestrator halts; `Notifier.notify(RunDeadlocked)` called; prints blocked tasks to console |
| `state.json` corrupt on resume | Log the corruption; treat all tasks as PENDING and reconstruct from PRs + worktrees |
| Worktree in inconsistent state | Agent attempts autonomous recovery; if it can't, marks task FAILED |

All errors are logged at `error` level with the task ID, error type, and message. The orchestrator never exits silently.

---

## 12. Testing Strategy

| Test type | Scope | What is mocked |
|-----------|-------|---------------|
| Unit | Individual service/store classes | All I/O (file system, HTTP, shell) |
| Integration | Input layer validation loop | Claude API (recorded responses); real filesystem |
| Integration | Orchestrator lifecycle | Claude API; `FakeVcsProvider`; real filesystem and git |
| End-to-end | Full run against a toy task list | Nothing — requires real credentials |

Unit and integration tests run via `./gradlew :agentic:release`. End-to-end tests are tagged `@E2E` and excluded from the standard build.

---

## Open Questions

| # | Question | Impact |
|---|----------|--------|
| ~~1~~ | ~~Should `validate` block and wait for human to fix issues, or just report and exit?~~ | **Resolved:** report and exit. The human re-runs `agentic validate` after editing docs. |
| ~~2~~ | ~~How does the system notify the human when a task fails or a run deadlocks — console only, or also a VCS issue/comment?~~ | **Resolved:** `Notifier` abstraction; default impl posts VCS PR comments. See §5.5 and §7.2. |
| ~~3~~ | ~~Should worktrees be created lazily (on assignment) or eagerly (on run start)?~~ | **Resolved:** lazy creation on agent assignment; deleted immediately on DONE or FAILED. |
| ~~4~~ | ~~Should `state.json` be committed to the repo for easier team visibility, or stay gitignored?~~ | **Resolved:** gitignored. The entire `.agentic/` directory is excluded from version control. |

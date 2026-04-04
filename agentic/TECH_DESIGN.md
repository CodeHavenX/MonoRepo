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

`Task` is a pure definition — it carries no runtime state. Status, PR info, and worktree path are all derived from durable artifacts at query time (see `StateDeriver`, §5.2).

```kotlin
@Serializable
data class Task(
    val id: String,
    val title: String,
    val description: String,
    val dependencies: List<String>,   // task IDs
    val timeoutSeconds: Long = 3600L,
)

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

Amendments are not tracked as data models. An amendment is a VCS pull request — its lifecycle (open, merged, rejected) is read directly from the VCS provider. The agent writes a marker file (see §6) while waiting for a critical amendment; no in-process state is maintained.

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
/** Parses task definitions from the validated TASK_LIST document. Read-only. */
interface TaskStore {
    fun getAll(): List<Task>
    fun get(id: String): Task
}

/**
 * Derives the current status of any task entirely from durable artifacts —
 * VCS PR state, worktree presence, and per-task marker files.
 * No in-memory state is consulted; safe to call after a crash and restart.
 *
 * Derivation order:
 *   1. Merged PR for branch `agentic/{task-id}` → DONE
 *   2. failed.txt marker exists                 → FAILED
 *   3. Open PR + changes requested              → IN_PROGRESS (needs agent re-run)
 *   4. Open PR                                  → IN_REVIEW
 *   5. Worktree exists                          → IN_PROGRESS
 *   6. All dependencies DONE                    → PENDING
 *   7. Otherwise                                → BLOCKED
 */
interface StateDeriver {
    suspend fun statusOf(task: Task): TaskStatus
}

/** Scores tasks by downstream dependency count for critical-path assignment. */
interface DependencyGraph {
    fun downstreamCount(taskId: String): Int
}

/**
 * Top-level coordinator. [run] starts or resumes a run — the distinction is
 * irrelevant because state is always re-derived from disk on startup.
 * Blocks until all tasks are DONE or the run deadlocks.
 */
interface Orchestrator {
    suspend fun run(config: OrchestratorConfig)
    /** Derives and returns the current status of every task. */
    suspend fun status(): Map<Task, TaskStatus>
}

data class OrchestratorConfig(
    val agentPoolSize: Int,
    val pollIntervalSeconds: Long = 30L,
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
 * Worktrees are created lazily — [getOrCreate] is called only when an agent is
 * assigned a task. [delete] is called immediately when a task reaches DONE or FAILED.
 */
interface WorktreeManager {
    /** Returns the existing worktree, or creates one if it does not yet exist. */
    fun getOrCreate(taskId: String): Worktree
    /** Returns the existing worktree, or null if it has not been created. */
    fun get(taskId: String): Worktree?
    /** Lists all worktrees currently on disk. Used by StateDeriver. */
    fun listAll(): List<Worktree>
    /** Deletes the worktree. Called on task DONE or FAILED. */
    fun delete(taskId: String)
}

data class Worktree(
    val taskId: String,
    val path: Path,
    val branchName: String,
)

/**
 * Runs an agent against a task. Returns when the agent opens a PR or gives up.
 * Critical amendment waits are handled internally — the agent polls VCS itself
 * and writes/clears the awaiting-amendment.txt marker file. No external coordination needed.
 */
interface AgentRunner {
    suspend fun run(task: Task, worktree: Worktree): AgentResult
}

sealed class AgentResult {
    /** Agent opened or updated a PR; task is now IN_REVIEW. */
    data class PrOpened(val prId: String, val prUrl: String) : AgentResult()
    /** Agent could not complete the task; failed.txt has been written. */
    data class Failed(val reason: String) : AgentResult()
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

All durable state lives under `.agentic/` in the repository root. There is no single state snapshot file — task status is derived from the files that exist, not from a stored value.

```
.agentic/
  config.json               # OrchestratorConfig (written by `agentic init`)
  docs/
    goals-scope.md
    architecture-design.md
    standards.md
    task-list.md
    validation-report.md    # Generated; updated after each validation pass
  tasks/
    {task-id}/
      failed.txt            # Present only when FAILED; contains the human-readable reason
      awaiting-amendment.txt# Present only while agent is paused for a critical amendment;
                            # contains the amendment PR ID
  worktrees/
    {task-id}/              # Git worktree; presence signals IN_PROGRESS or IN_REVIEW
```

**How status is encoded in files:**

| What exists | Derived status |
|-------------|---------------|
| Merged PR for `agentic/{task-id}` | DONE |
| `failed.txt` | FAILED |
| Open PR with changes requested | IN_PROGRESS |
| Open PR | IN_REVIEW |
| Worktree directory | IN_PROGRESS |
| No worktree, all deps DONE | PENDING |
| No worktree, a dep not DONE | BLOCKED |

**Marker files are human-readable plain text.** `failed.txt` contains the failure reason as a sentence; `awaiting-amendment.txt` contains one line: the amendment PR URL.

**Worktree naming:** Branch names follow the pattern `agentic/{task-id}`. The path `.agentic/worktrees/{task-id}` is implicit — derived from the task ID alone.

**`.agentic/` in `.gitignore`:** The entire directory is excluded from version control. Docs authored by the human live in a separate human-managed location (e.g., `agentic-docs/`) referenced by path in `config.json`.

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

The orchestrator is a poll loop. On every tick it re-derives all task statuses from disk and VCS, launches agents for tasks that need one, and checks whether the run is finished. No events, no channels, no stored state.

The only in-memory state is `activeTaskIds: Set<String>` — the set of task IDs that have a running agent coroutine right now. This is intentionally not persisted: on restart it is empty, and the poll loop re-derives which tasks need an agent from the filesystem.

```
suspend fun run(config: OrchestratorConfig) {
    val tasks = taskStore.getAll()
    val activeTaskIds = mutableSetOf<String>()

    while (true) {
        // 1. Derive current status of every task from filesystem + VCS
        val statuses: Map<Task, TaskStatus> = tasks.associateWith { stateDeriver.statusOf(it) }

        // 2. Check termination
        when {
            statuses.values.all { it == DONE } -> {
                notifier.notify(RunCompleted(tasks))
                return
            }
            statuses.values.none { it == IN_PROGRESS || it == PENDING } -> {
                notifier.notify(RunDeadlocked(...))
                return
            }
        }

        // 3. Launch agents for tasks that need one
        val freeSlots = config.agentPoolSize - activeTaskIds.size
        statuses.entries
            .filter { (task, status) ->
                (status == PENDING || status == IN_PROGRESS) && task.id !in activeTaskIds
            }
            .sortedByDescending { (task, _) -> dependencyGraph.downstreamCount(task.id) }
            .take(freeSlots)
            .forEach { (task, _) ->
                val worktree = worktreeManager.getOrCreate(task.id)
                activeTaskIds += task.id
                launch(Dispatchers.IO) {
                    try { agentRunner.run(task, worktree) }
                    finally { activeTaskIds -= task.id }
                }
            }

        // 4. Wait before next tick
        delay(config.pollIntervalSeconds * 1_000)
    }
}
```

**Why this works after a crash:** on restart `activeTaskIds` is empty. `StateDeriver` sees existing worktrees and open PRs and returns the correct statuses. The poll loop launches agents for any IN_PROGRESS task without a running coroutine, and the agent picks up from the existing worktree state.

**IN_REVIEW tasks** do not occupy an agent slot. The agent coroutine exits after opening a PR. If the reviewer requests changes, `StateDeriver` returns IN_PROGRESS on the next tick (open PR + changes requested) and a new agent is launched against the same worktree.

**Timeouts** are enforced inside `AgentRunner`. If the agent exceeds `task.timeoutSeconds`, the runner writes `failed.txt` and returns `AgentResult.Failed`. The poll loop sees FAILED on the next tick and calls `notifier.notify(TaskFailed)`.

---

## 11. Error Handling

| Failure | Handling |
|---------|---------|
| Agent signals `task_failed` | `AgentRunner` writes `failed.txt`; next poll tick derives FAILED; `Notifier.notify(TaskFailed)` called |
| Agent exceeds timeout | `AgentRunner` enforces timeout, writes `failed.txt`, returns `Failed`; same as above |
| VCS provider call fails | Retry up to 3 times with exponential backoff; if still failing, `AgentRunner` writes `failed.txt` |
| Claude API returns error | Retry transient errors (5xx, 429); persistent errors cause `AgentRunner` to write `failed.txt` |
| Deadlock detected | Poll loop detects no PENDING or IN_PROGRESS tasks; `Notifier.notify(RunDeadlocked)` called; orchestrator exits |
| Process crash mid-run | No recovery needed — next `agentic run` re-derives state from filesystem and resumes |
| Worktree in inconsistent state | Agent attempts autonomous recovery on resume; if not feasible, writes `failed.txt` |

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

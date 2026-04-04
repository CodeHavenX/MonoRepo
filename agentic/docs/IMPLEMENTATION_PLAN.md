# Agentic Module — Low-Level Implementation Plan

**Document status:** Ready for engineering handoff
**Module root:** `/home/cramsan/git/MonoRepo/agentic/`
**Package root:** `com.cramsan.agentic`
**Kotlin version:** 2.3.20 (matches monorepo)
**JVM toolchain:** JDK 21 (matches monorepo)

---

## Conventions and Ground Rules

Before the phase breakdown, the following monorepo conventions apply everywhere in this plan:

- All Gradle version strings use `_` (refreshVersions placeholder), not literals.
- `kotlin-jvm-target-application.gradle` is applied via `apply(from = ...)` — it transitively applies `kotlin-jvm-target-lib.gradle`, which already pulls in `kotlin-stdlib-jdk8`, `kotlinx-coroutines-core`, and the full JUnit5 + MockK test setup.
- The `release` task is created by `release-task.gradle` and is inherited from `kotlin-jvm-target-lib.gradle`. The `releaseJvm` task that backs it runs `build`, `detektMain`, and `test`.
- The `integTest` source set pattern follows `edifikana/back-end` exactly: it creates a source set, extends `testImplementation` configuration, registers a `integTest` task, and makes `release` depend on `compileIntegTestKotlin`.
- DI wiring uses Koin (`io.insert-koin:koin-core:_`) with `module { }` blocks in `app/` as the composition root.
- Logging uses `EventLogger.singleton` (global object) and the top-level helpers `logI`, `logD`, `logW`, `logE` from `framework:interfacelib`.
- All data classes in `core/` that are persisted to JSON are annotated with `@Serializable`.
- Shell process execution is done with `ProcessBuilder` wrapped in a suspend function dispatched on `Dispatchers.IO`.
- Clikt is not currently in `versions.properties`; it must be added as `version.com.github.ajalt.clikt..clikt=4.x.x`.

---

## Phase 1 — Gradle Module Setup and Core Models

### P1-T1: Gradle Module Registration

**Files to create/modify:**

- `agentic/build.gradle.kts` — create
- `/home/cramsan/git/MonoRepo/settings.gradle.kts` — modify (add `include("agentic")`)
- `/home/cramsan/git/MonoRepo/build.gradle.kts` — modify (add `dependsOn("agentic:release")` to `releaseAll`)
- `/home/cramsan/git/MonoRepo/versions.properties` — modify (add Clikt version entry)

**What to implement:**

`agentic/build.gradle.kts` structure:

```
plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

val mainClassTarget by extra("com.cramsan.agentic.app.MainKt")
val jarNameTarget by extra("agentic")

apply(from = "$rootDir/gradle/kotlin-jvm-target-application.gradle")
```

- Declare `sourceSets { val integTest by creating { ... } }` block identical to the pattern in `edifikana/back-end/build.gradle.kts`: java + kotlin + resources src dirs, `compileClasspath` and `runtimeClasspath` wired from `main` + `test` output.
- Declare `configurations { getByName("integTestImplementation") { extendsFrom(...) } }`.
- Register `tasks.register<Test>("integTest")` with `useJUnitPlatform()`, `shouldRunAfter("test")`.
- Override `tasks.getByName("release")` to `dependsOn("compileIntegTestKotlin")`.
- In `dependencies` block include: `framework:interfacelib`, `framework:logging`, `framework:utils`, `framework:test` (testImplementation), `kotlinx-serialization-json`, `ktor-client-core`, `ktor-client-cio`, `ktor-client-content-negotiation`, `ktor-serialization-kotlinx-json`, `koin-core` (for DI), `clikt`.

In `versions.properties`, add:
```
version.com.github.ajalt.clikt..clikt=4.4.0
```

In `settings.gradle.kts`, add `include("agentic")` after the existing includes.

In `build.gradle.kts` root, add `dependsOn("agentic:release")` inside `releaseAll`.

**Dependencies:** None (first task)
**Unit test task:** N/A — this task is build configuration only

---

### P1-T2: Core Data Models

**Files to create:**

- `agentic/src/main/kotlin/com/cramsan/agentic/core/AgenticDocument.kt`
- `agentic/src/main/kotlin/com/cramsan/agentic/core/ValidationModels.kt`
- `agentic/src/main/kotlin/com/cramsan/agentic/core/Task.kt`
- `agentic/src/main/kotlin/com/cramsan/agentic/core/ReviewerModels.kt`
- `agentic/src/main/kotlin/com/cramsan/agentic/core/VcsModels.kt`
- `agentic/src/main/kotlin/com/cramsan/agentic/core/AgenticConfig.kt`
- `agentic/src/main/kotlin/com/cramsan/agentic/core/ClaudeModels.kt`

**What to implement:**

`AgenticDocument.kt`:
- `@Serializable data class AgenticDocument(val id: String, val type: DocumentType, val relativePath: String, val status: DocumentStatus, val lastModifiedEpochMs: Long)`
- `@Serializable enum class DocumentType { GOALS_SCOPE, ARCHITECTURE_DESIGN, STANDARDS, TASK_LIST }`
- `@Serializable enum class DocumentStatus { UNREVIEWED, IN_REVIEW, NEEDS_REVISION, VALIDATED }`

`ValidationModels.kt`:
- `@Serializable data class ValidationIssue(val id: String, val documentId: String, val description: String, val severity: IssueSeverity, val status: IssueStatus)`
- `@Serializable enum class IssueSeverity { BLOCKING, ADVISORY }`
- `@Serializable enum class IssueStatus { OPEN, ADDRESSED, DISMISSED }`
- `@Serializable data class ValidationReport(val runId: String, val timestampEpochMs: Long, val issues: List<ValidationIssue>)`

`Task.kt`:
- `@Serializable data class Task(val id: String, val title: String, val description: String, val dependencies: List<String>, val timeoutSeconds: Long = 3600L)`
- `enum class TaskStatus { PENDING, IN_PROGRESS, IN_REVIEW, DONE, BLOCKED, FAILED }` — note: NOT `@Serializable` because status is always derived, never persisted

`ReviewerModels.kt`:
- `data class ReviewerDefinition(val name: String, val systemPrompt: String)` — not serialized; loaded from raw file content
- `data class ReviewerFeedback(val reviewerName: String, val content: String)`

`VcsModels.kt`:
- `@Serializable data class PullRequest(val id: String, val url: String, val title: String, val state: PullRequestState, val sourceBranch: String, val targetBranch: String, val labels: List<String>)`
- `@Serializable enum class PullRequestState { OPEN, CLOSED, MERGED }`
- `@Serializable data class PullRequestComment(val author: String, val body: String, val createdAtEpochMs: Long)`

`AgenticConfig.kt`:
- `@Serializable data class AgenticConfig(val agentPoolSize: Int, val defaultTaskTimeoutSeconds: Long = 3600L, val baseBranch: String, val claudeModel: String = "claude-opus-4-6", val docsDir: String, val anthropicApiKeyEnvVar: String = "ANTHROPIC_API_KEY", val vcsProvider: VcsProviderConfig)`
- `@Serializable sealed class VcsProviderConfig { @Serializable @SerialName("github") data class GitHub(val owner: String, val repo: String) : VcsProviderConfig() }`

`ClaudeModels.kt`:
- `@Serializable data class ClaudeMessage(val role: String, val content: String)`
- `@Serializable data class ClaudeTool(val name: String, val description: String, val inputSchema: kotlinx.serialization.json.JsonObject)`
- `@Serializable data class ClaudeResponse(val id: String, val content: List<ClaudeContentBlock>, val stopReason: String?)`
- `@Serializable sealed class ClaudeContentBlock` with `@Serializable @SerialName("text") data class Text(val text: String)` and `@Serializable @SerialName("tool_use") data class ToolUse(val id: String, val name: String, val input: kotlinx.serialization.json.JsonObject)`

**Dependencies:** P1-T1
**Unit test task:** P1-T3

---

### P1-T3: Core Model Unit Tests

**Files to create:**

- `agentic/src/test/kotlin/com/cramsan/agentic/core/AgenticDocumentTest.kt`
- `agentic/src/test/kotlin/com/cramsan/agentic/core/AgenticConfigTest.kt`
- `agentic/src/test/kotlin/com/cramsan/agentic/core/VcsModelsTest.kt`

**What to implement:**

- Verify round-trip JSON serialization for every `@Serializable` model using `Json.encodeToString` / `Json.decodeFromString`.
- Verify that `VcsProviderConfig` sealed class polymorphic serialization works with the `@SerialName` discriminator: encode a `GitHub` instance, decode it back, assert equality.
- Verify that `DocumentStatus` enum values serialize to their string names.
- No mocking needed — pure data/serialization tests.

**Dependencies:** P1-T2
**Unit test task:** Self-contained

---

## Phase 2 — Input Layer

### P2-T1: Interface Definitions for Input Layer

**Files to create:**

- `agentic/src/main/kotlin/com/cramsan/agentic/input/Scaffolder.kt`
- `agentic/src/main/kotlin/com/cramsan/agentic/input/DocumentStore.kt`
- `agentic/src/main/kotlin/com/cramsan/agentic/input/ValidationService.kt`

**What to implement:**

`Scaffolder.kt`:
- `interface Scaffolder { fun scaffold(outputDir: java.nio.file.Path) }`

`DocumentStore.kt`:
- `interface DocumentStore { fun getAll(): List<AgenticDocument>; fun get(id: String): AgenticDocument; fun updateStatus(id: String, status: DocumentStatus); fun onDocumentChanged(); fun allValidated(): Boolean }`

`ValidationService.kt`:
- `interface ValidationService { suspend fun reviewDocument(document: AgenticDocument): List<ValidationIssue>; suspend fun runValidationPass(): ValidationReport }`

**Dependencies:** P1-T2
**Unit test task:** N/A — interfaces only

---

### P2-T2: DefaultScaffolder

**Files to create:**

- `agentic/src/main/kotlin/com/cramsan/agentic/input/DefaultScaffolder.kt`

**What to implement:**

`class DefaultScaffolder : Scaffolder`

- `scaffold(outputDir: Path)`: creates `outputDir/goals-scope.md`, `outputDir/architecture-design.md`, `outputDir/standards.md`, `outputDir/task-list.md` with multi-line placeholder strings embedded as string literals in the class.
- Creates `outputDir/reviewers/security.md` and `outputDir/reviewers/design-patterns.md` with template system prompt text.
- Uses `java.nio.file.Files.createDirectories` for parent dirs and `java.nio.file.Files.writeString` for file content.
- Logs each created file path at INFO level using `logI(TAG, "Scaffolded: $path")`.
- If a file already exists, log a WARNING and skip (do not overwrite).

**Dependencies:** P2-T1
**Unit test task:** P2-T3

---

### P2-T3: DefaultScaffolder Unit Tests

**Files to create:**

- `agentic/src/test/kotlin/com/cramsan/agentic/input/DefaultScaffolderTest.kt`

**What to implement:**

- Use `java.nio.file.Files.createTempDirectory` for an isolated temp dir per test (JUnit5 `@TempDir` annotation).
- Assert all six expected files are created.
- Assert file content is non-empty and contains expected section markers.
- Test idempotency: call `scaffold` twice and assert no exception, pre-existing files are not overwritten.

**Dependencies:** P2-T2
**Unit test task:** Self-contained

---

### P2-T4: FileSystemDocumentStore

**Files to create:**

- `agentic/src/main/kotlin/com/cramsan/agentic/input/FileSystemDocumentStore.kt`

**What to implement:**

`class FileSystemDocumentStore(private val docsDir: java.nio.file.Path, private val json: Json) : DocumentStore`

- Internal state: `private val documents: MutableMap<String, AgenticDocument>` initialized lazily from disk.
- `getAll()`: returns `documents.values.toList()`.
- `get(id: String)`: returns `documents[id] ?: throw IllegalArgumentException("Unknown document id: $id")`.
- `updateStatus(id, status)`: updates in-memory map, then writes the updated state as a JSON sidecar file at `docsDir/.agentic-meta/{id}.json`. This JSON file stores only `id`, `status`, and `lastModifiedEpochMs`.
- `onDocumentChanged()`: resets all entries in `documents` to `DocumentStatus.UNREVIEWED` and rewrites all sidecar files.
- `allValidated()`: returns `documents.values.all { it.status == DocumentStatus.VALIDATED }`.
- Initialization: scans `docsDir` for the four expected document files by name pattern; for each found file, reads the corresponding sidecar (if any) to restore status; otherwise assigns `UNREVIEWED`. Uses `lastModified()` for `lastModifiedEpochMs`.

**Dependencies:** P2-T1, P1-T2
**Unit test task:** P2-T5

---

### P2-T5: FileSystemDocumentStore Unit Tests

**Files to create:**

- `agentic/src/test/kotlin/com/cramsan/agentic/input/FileSystemDocumentStoreTest.kt`

**What to implement:**

- All file I/O uses a `@TempDir` temp directory.
- Test `getAll()` returns the correct number of documents when files are present.
- Test `updateStatus()` persists the new status and is readable after a fresh construction (simulating restart).
- Test `onDocumentChanged()` resets all statuses to UNREVIEWED.
- Test `allValidated()` returns false until all are set to VALIDATED.
- No mocking of the filesystem; use the real FS with temp dir.

**Dependencies:** P2-T4
**Unit test task:** Self-contained

---

### P2-T6: DefaultValidationService

**Files to create:**

- `agentic/src/main/kotlin/com/cramsan/agentic/input/DefaultValidationService.kt`

**What to implement:**

`class DefaultValidationService(private val documentStore: DocumentStore, private val claudeClient: ClaudeClient, private val model: String, private val reviewerAgents: List<ReviewerAgent>, private val reviewerLoader: ReviewerLoader, private val json: Json) : ValidationService`

- `reviewDocument(document: AgenticDocument): List<ValidationIssue>`:
  1. Reads the document's file content from disk using `document.relativePath`.
  2. Calls `claudeClient.chat(...)` with a system prompt instructing the agent to return JSON-formatted issues (a `List<ValidationIssue>`).
  3. Parses the response text as JSON into `List<ValidationIssue>`.
  4. Updates `documentStore.updateStatus(document.id, NEEDS_REVISION)` if any BLOCKING issues exist, else `VALIDATED`.
  5. Returns the issues.

- `runValidationPass(): ValidationReport`:
  1. Calls `documentStore.getAll()` and marks each as `IN_REVIEW` via `updateStatus`.
  2. Reviews each document sequentially (not parallel, to keep token usage predictable): calls `reviewDocument(doc)` for each.
  3. Collects all issues.
  4. Loads reviewer definitions via `reviewerLoader.loadAll()`.
  5. If reviewers exist, runs `coroutineScope { reviewerAgents.map { agent -> async { ... } }.awaitAll() }` for parallel reviewer execution against all documents.
  6. Prints reviewer outputs to stdout (caller is CLI, but the service does the console output for reviewer feedback only).
  7. Constructs and returns `ValidationReport(runId = UUID.randomUUID().toString(), timestampEpochMs = System.currentTimeMillis(), issues = allIssues)`.
  8. Writes the report as JSON to `docsDir/validation-report.md` (human-readable markdown wrapping the JSON).

**Dependencies:** P2-T1, P1-T2, P2-T4 (DocumentStore), Phase 8 (ClaudeClient), Phase 9 (ReviewerAgent/ReviewerLoader) — declare dependencies on interfaces, not implementations. Wire together in Phase 10.
**Unit test task:** P2-T7

---

### P2-T7: DefaultValidationService Unit Tests

**Files to create:**

- `agentic/src/test/kotlin/com/cramsan/agentic/input/DefaultValidationServiceTest.kt`

**What to implement:**

- MockK mocks for `DocumentStore`, `ClaudeClient`, `ReviewerAgent`, `ReviewerLoader`.
- Test `reviewDocument`: verify `claudeClient.chat` is called with the correct system prompt structure, verify `documentStore.updateStatus` is called with VALIDATED when no blocking issues returned, NEEDS_REVISION when blocking issues exist.
- Test `runValidationPass`: verify it iterates over all documents, calls `reviewDocument` for each, invokes all reviewer agents in parallel (assert all `reviewDocument` calls on the reviewer agents were made), and returns a report with the aggregated issues.
- Use `runCoroutineTest` from `framework:test`'s `CoroutineTest`.

**Dependencies:** P2-T6
**Unit test task:** Self-contained

---

## Phase 3 — VCS Provider Abstraction

### P3-T1: VcsProvider Interface

**Files to create:**

- `agentic/src/main/kotlin/com/cramsan/agentic/vcs/VcsProvider.kt`

**What to implement:**

`interface VcsProvider` with all methods as defined in TECH_DESIGN.md §5.4:
- `suspend fun createPullRequest(sourceBranch: String, targetBranch: String, title: String, body: String, labels: List<String> = emptyList()): PullRequest`
- `suspend fun listOpenPullRequests(labels: List<String> = emptyList()): List<PullRequest>`
- `suspend fun listMergedPullRequests(labels: List<String> = emptyList()): List<PullRequest>`
- `suspend fun getPullRequestComments(prId: String): List<PullRequestComment>`
- `suspend fun addPullRequestComment(prId: String, body: String)`
- `suspend fun isPullRequestMerged(prId: String): Boolean`
- `suspend fun pullRequestHasRequestedChanges(prId: String): Boolean`

**Dependencies:** P1-T2
**Unit test task:** N/A — interface only

---

### P3-T2: FakeVcsProvider

**Files to create:**

- `agentic/src/main/kotlin/com/cramsan/agentic/vcs/fake/FakeVcsProvider.kt`

**What to implement:**

`class FakeVcsProvider : VcsProvider`

Internal state (all mutable and directly accessible from tests):
- `val pullRequests: MutableList<PullRequest> = mutableListOf()`
- `val comments: MutableMap<String, MutableList<PullRequestComment>> = mutableMapOf()`
- `val requestedChangesForPr: MutableSet<String> = mutableSetOf()`
- `private var nextPrId = 1`

Methods:
- `createPullRequest(...)`: constructs a `PullRequest` with `id = nextPrId++.toString()`, appends to `pullRequests`, returns it.
- `listOpenPullRequests(labels)`: filters `pullRequests` by `state == OPEN` and matching labels (empty labels = no label filter).
- `listMergedPullRequests(labels)`: same but `state == MERGED`.
- `getPullRequestComments(prId)`: returns `comments[prId] ?: emptyList()`.
- `addPullRequestComment(prId, body)`: appends a `PullRequestComment` to `comments[prId]`.
- `isPullRequestMerged(prId)`: returns `pullRequests.first { it.id == prId }.state == MERGED`.
- `pullRequestHasRequestedChanges(prId)`: returns `prId in requestedChangesForPr`.

Test-facing helpers:
- `fun mergePullRequest(prId: String)`: sets the state of the matching PR to `MERGED`.
- `fun requestChanges(prId: String)`: adds `prId` to `requestedChangesForPr`.

**Dependencies:** P3-T1
**Unit test task:** P3-T4 (tested as part of the integration test, plus a standalone unit test)

---

### P3-T3: GitHubVcsProvider

**Files to create:**

- `agentic/src/main/kotlin/com/cramsan/agentic/vcs/github/GitHubVcsProvider.kt`
- `agentic/src/main/kotlin/com/cramsan/agentic/vcs/github/ShellRunner.kt`

**What to implement:**

`ShellRunner.kt`:
- `class ShellRunner` with a single method: `suspend fun run(vararg args: String): ShellResult` where `ShellResult` is `data class ShellResult(val stdout: String, val exitCode: Int, val stderr: String)`.
- Internally uses `ProcessBuilder(*args).also { it.redirectErrorStream(false) }.start()` and reads stdout/stderr on `Dispatchers.IO`.
- Retries transient non-zero exit codes up to 3 times with 1s, 2s, 4s exponential backoff.

`GitHubVcsProvider.kt`:
- `class GitHubVcsProvider(private val owner: String, private val repo: String, private val shell: ShellRunner, private val json: Json) : VcsProvider`
- `createPullRequest(...)`: runs `gh pr create --title ... --body ... --base $targetBranch --head $sourceBranch --label $label --json number,url,title,state,headRefName`. Parses result JSON.
- `listOpenPullRequests(labels)`: runs `gh pr list --state open --label $label --json number,url,title,state,headRefName,baseRefName,labels`. Maps JSON array to `List<PullRequest>`.
- `listMergedPullRequests(labels)`: same but `--state merged`.
- `getPullRequestComments(prId)`: runs `gh pr view $prId --json comments`. Maps JSON to `List<PullRequestComment>`.
- `addPullRequestComment(prId, body)`: runs `gh pr comment $prId --body "$body"`.
- `isPullRequestMerged(prId)`: runs `gh pr view $prId --json mergedAt`. Returns true if `mergedAt` is non-null.
- `pullRequestHasRequestedChanges(prId)`: runs `gh pr view $prId --json reviewDecision`. Returns true if value is `"CHANGES_REQUESTED"`.
- All methods are `suspend` and dispatched on `Dispatchers.IO`.
- Each method catches `ShellResult.exitCode != 0` and throws a typed `VcsProviderException(message, exitCode)`.

**Dependencies:** P3-T1, P1-T2
**Unit test task:** P3-T4

---

### P3-T4: VcsProvider Unit Tests

**Files to create:**

- `agentic/src/test/kotlin/com/cramsan/agentic/vcs/fake/FakeVcsProviderTest.kt`
- `agentic/src/test/kotlin/com/cramsan/agentic/vcs/github/GitHubVcsProviderTest.kt`

**What to implement:**

`FakeVcsProviderTest.kt`:
- Tests that `createPullRequest` returns a PR with state OPEN and increments IDs.
- Tests `mergePullRequest` causes `isPullRequestMerged` to return true.
- Tests `requestChanges` causes `pullRequestHasRequestedChanges` to return true.
- Tests `addPullRequestComment` is reflected in `getPullRequestComments`.

`GitHubVcsProviderTest.kt`:
- Mock `ShellRunner` using MockK.
- Test `createPullRequest`: verify the correct `gh` command is assembled, mock `ShellRunner.run` to return a canned JSON response, verify the returned `PullRequest` fields match.
- Test `getPullRequestComments`: verify JSON comment array is correctly mapped.
- Test `pullRequestHasRequestedChanges`: mock response with `reviewDecision: "CHANGES_REQUESTED"` → true; `"APPROVED"` → false.
- Test failure path: when `ShellRunner.run` returns `exitCode != 0`, assert `VcsProviderException` is thrown.

**Dependencies:** P3-T2, P3-T3
**Unit test task:** Self-contained

---

## Phase 4 — Notifications

### P4-T1: Notifier Interface and AgenticEvent Models

**Files to create:**

- `agentic/src/main/kotlin/com/cramsan/agentic/notification/Notifier.kt`
- `agentic/src/main/kotlin/com/cramsan/agentic/notification/AgenticEvent.kt`

**What to implement:**

`Notifier.kt`:
- `interface Notifier { suspend fun notify(event: AgenticEvent) }`

`AgenticEvent.kt`:
- `sealed class AgenticEvent`:
  - `data class TaskFailed(val task: Task, val reason: String) : AgenticEvent()`
  - `data class RunDeadlocked(val blockedTasks: List<Task>, val failedTasks: List<Task>) : AgenticEvent()`
  - `data class RunCompleted(val completedTasks: List<Task>) : AgenticEvent()`

**Dependencies:** P1-T2
**Unit test task:** N/A — interfaces/sealed class only

---

### P4-T2: FakeNotifier

**Files to create:**

- `agentic/src/main/kotlin/com/cramsan/agentic/notification/fake/FakeNotifier.kt`

**What to implement:**

`class FakeNotifier : Notifier`:
- Internal state: `val receivedEvents: MutableList<AgenticEvent> = mutableListOf()`
- `notify(event)`: appends `event` to `receivedEvents`.
- Test-facing helper: `fun clear()` resets the list.

**Dependencies:** P4-T1
**Unit test task:** Exercised via orchestrator tests in Phase 6

---

### P4-T3: VcsCommentNotifier

**Files to create:**

- `agentic/src/main/kotlin/com/cramsan/agentic/notification/vcs/VcsCommentNotifier.kt`

**What to implement:**

`class VcsCommentNotifier(private val vcsProvider: VcsProvider) : Notifier`

- `notify(event)` implements the routing logic from TECH_DESIGN.md §7.2:
  - `TaskFailed`: calls `vcsProvider.listOpenPullRequests(labels = listOf("agentic-code"))`, finds the PR whose `sourceBranch == "agentic/${event.task.id}"`. If found, calls `vcsProvider.addPullRequestComment(pr.id, formatNotificationComment("Task Failed", "...reason..."))`. If not found, logs the event to console only.
  - `RunDeadlocked`: finds the most recently opened PR still in state OPEN via `vcsProvider.listOpenPullRequests(...)`. Posts a comment if found.
  - `RunCompleted`: logs to console only — no PR comment.
- `private fun formatNotificationComment(title: String, body: String): String` produces the comment string with `<!-- agentic-notification -->` header as defined in §7.2.
- Checks for existing notification comment before posting: calls `getPullRequestComments(prId)` and skips if any comment body contains `<!-- agentic-notification -->`.

**Dependencies:** P4-T1, P3-T1
**Unit test task:** P4-T4

---

### P4-T4: VcsCommentNotifier Unit Tests

**Files to create:**

- `agentic/src/test/kotlin/com/cramsan/agentic/notification/vcs/VcsCommentNotifierTest.kt`

**What to implement:**

- Mock `VcsProvider` with MockK.
- Test `TaskFailed` event: mock `listOpenPullRequests` to return a PR matching the task branch, assert `addPullRequestComment` is called with a body containing `<!-- agentic-notification -->`.
- Test `TaskFailed` with no open PR: assert `addPullRequestComment` is NOT called.
- Test duplicate notification prevention: mock `getPullRequestComments` to return a comment already containing `<!-- agentic-notification -->`, assert second `addPullRequestComment` is not called.
- Test `RunCompleted`: assert no PR API calls are made.

**Dependencies:** P4-T3
**Unit test task:** Self-contained

---

## Phase 5 — Coordination Layer

### P5-T1: TaskStore Interface and FileSystemTaskStore

**Files to create:**

- `agentic/src/main/kotlin/com/cramsan/agentic/coordination/TaskStore.kt`
- `agentic/src/main/kotlin/com/cramsan/agentic/coordination/FileSystemTaskStore.kt`

**What to implement:**

`TaskStore.kt`:
- `interface TaskStore { fun getAll(): List<Task>; fun get(id: String): Task }`

`FileSystemTaskStore.kt`:
- `class FileSystemTaskStore(private val taskListPath: java.nio.file.Path) : TaskStore`
- On first access (lazy), reads the markdown task list file at `taskListPath`.
- Parses it using a simple regex/line-by-line parser that recognizes a task block format. The format is: each task starts with a line `## Task: {id}` (or a similar structured heading agreed upon during scaffolding), followed by `Title:`, `Description:`, `Dependencies:`, `Timeout:` fields. All fields are parsed into `Task` objects.
- The `getAll()` and `get(id)` methods are backed by the parsed list.
- Throws `IllegalStateException` if the file cannot be parsed or a referenced task ID does not exist.
- Logs parsed task count at INFO level.

**Dependencies:** P1-T2
**Unit test task:** P5-T2

---

### P5-T2: FileSystemTaskStore Unit Tests

**Files to create:**

- `agentic/src/test/kotlin/com/cramsan/agentic/coordination/FileSystemTaskStoreTest.kt`

**What to implement:**

- Use a `@TempDir` with a synthetic `task-list.md` file written in the expected format.
- Test `getAll()` returns correctly structured `Task` objects with correct IDs, titles, descriptions, dependencies.
- Test a task with no dependencies has `dependencies = emptyList()`.
- Test a task with multiple dependencies lists all dependency IDs.
- Test `get(unknownId)` throws an exception.

**Dependencies:** P5-T1
**Unit test task:** Self-contained

---

### P5-T3: DependencyGraph

**Files to create:**

- `agentic/src/main/kotlin/com/cramsan/agentic/coordination/DependencyGraph.kt`
- `agentic/src/main/kotlin/com/cramsan/agentic/coordination/DefaultDependencyGraph.kt`

**What to implement:**

`DependencyGraph.kt`:
- `interface DependencyGraph { fun downstreamCount(taskId: String): Int }`

`DefaultDependencyGraph.kt`:
- `class DefaultDependencyGraph(private val tasks: List<Task>) : DependencyGraph`
- Constructor builds a reverse adjacency map: `dependents: Map<String, Set<String>>` where `dependents[taskId]` is the set of all task IDs that directly depend on it.
- `downstreamCount(taskId: String): Int`: performs a BFS/DFS from `taskId` using the `dependents` map, counting all reachable nodes (transitive dependents). Returns 0 if no dependents.

**Dependencies:** P1-T2
**Unit test task:** P5-T4

---

### P5-T4: DependencyGraph Unit Tests

**Files to create:**

- `agentic/src/test/kotlin/com/cramsan/agentic/coordination/DefaultDependencyGraphTest.kt`

**What to implement:**

- Construct a small task list with a known graph (e.g., A → B → C and A → D → C) as pure in-memory `Task` objects.
- Assert `downstreamCount("C")` is 0.
- Assert `downstreamCount("B")` is 1.
- Assert `downstreamCount("A")` counts all transitively dependent nodes correctly.
- Test with a linear chain.
- Test with a single node (no dependencies).

**Dependencies:** P5-T3
**Unit test task:** Self-contained

---

### P5-T5: StateDeriver Interface and DefaultStateDeriver

**Files to create:**

- `agentic/src/main/kotlin/com/cramsan/agentic/coordination/StateDeriver.kt`
- `agentic/src/main/kotlin/com/cramsan/agentic/coordination/DefaultStateDeriver.kt`

**What to implement:**

`StateDeriver.kt`:
- `interface StateDeriver { suspend fun statusOf(task: Task): TaskStatus }`

`DefaultStateDeriver.kt`:
- `class DefaultStateDeriver(private val vcsProvider: VcsProvider, private val worktreeManager: WorktreeManager, private val agenticDir: java.nio.file.Path) : StateDeriver`

- `statusOf(task: Task): TaskStatus`: implements the derivation rules from TECH_DESIGN.md §5.2 in strict priority order:
  1. Check `vcsProvider.listMergedPullRequests(labels = listOf("agentic-code"))` for a PR whose `sourceBranch == "agentic/${task.id}"` → return `DONE`.
  2. Check if `agenticDir.resolve("tasks/${task.id}/failed.txt").exists()` → return `FAILED`.
  3. Check `vcsProvider.listOpenPullRequests(...)` for matching branch + `vcsProvider.pullRequestHasRequestedChanges(pr.id)` → return `IN_PROGRESS`.
  4. Check open PR exists for branch → return `IN_REVIEW`.
  5. Check `worktreeManager.get(task.id) != null` → return `IN_PROGRESS`.
  6. Check that all dependency task IDs have been individually evaluated as `DONE` (recursive call, but memoized within the same pass by the caller Orchestrator) → return `PENDING`.
  7. Otherwise → return `BLOCKED`.

- Memoization within a single `statusOf` call is handled by the caller (Orchestrator), not by `StateDeriver`. `StateDeriver` is stateless between calls.

**Dependencies:** P3-T1, P5-T1, Phase 7 (WorktreeManager interface — declare dependency on interface only)
**Unit test task:** P5-T6

---

### P5-T6: StateDeriver Unit Tests

**Files to create:**

- `agentic/src/test/kotlin/com/cramsan/agentic/coordination/DefaultStateDeriverTest.kt`

**What to implement:**

- MockK mocks for `VcsProvider` and `WorktreeManager`.
- `@TempDir` for `agenticDir` to test `failed.txt` presence.
- Test each derivation rule in isolation:
  - Merged PR for correct branch → DONE.
  - `failed.txt` exists → FAILED.
  - Open PR + changes requested → IN_PROGRESS.
  - Open PR, no changes requested → IN_REVIEW.
  - No PR, worktree exists → IN_PROGRESS.
  - No PR, no worktree, all deps evaluated as DONE → PENDING.
  - No PR, no worktree, one dep is not DONE → BLOCKED.
- Use `runCoroutineTest` from `CoroutineTest`.

**Dependencies:** P5-T5
**Unit test task:** Self-contained

---

## Phase 6 — Orchestrator

### P6-T1: Orchestrator Interface and OrchestratorConfig

**Files to create:**

- `agentic/src/main/kotlin/com/cramsan/agentic/coordination/Orchestrator.kt`

**What to implement:**

`Orchestrator.kt`:
- `interface Orchestrator { suspend fun run(config: OrchestratorConfig); suspend fun status(): Map<Task, TaskStatus> }`
- `data class OrchestratorConfig(val agentPoolSize: Int, val pollIntervalSeconds: Long = 30L, val baseBranch: String, val claudeModel: String, val vcsProvider: VcsProviderConfig)`

**Dependencies:** P1-T2, P5-T5
**Unit test task:** N/A — interface only

---

### P6-T2: DefaultOrchestrator

**Files to create:**

- `agentic/src/main/kotlin/com/cramsan/agentic/coordination/DefaultOrchestrator.kt`

**What to implement:**

`class DefaultOrchestrator(private val taskStore: TaskStore, private val stateDeriver: StateDeriver, private val dependencyGraph: DependencyGraph, private val worktreeManager: WorktreeManager, private val agentRunner: AgentRunner, private val notifier: Notifier) : Orchestrator`

Implement `run(config: OrchestratorConfig)` following the pseudocode in TECH_DESIGN.md §10 exactly:

```
suspend fun run(config: OrchestratorConfig) {
    val tasks = taskStore.getAll()
    val activeTaskIds: MutableSet<String> = ConcurrentHashMap.newKeySet() // thread-safe
    coroutineScope {
        while (true) {
            // 1. Derive statuses, using a locally-memoized map for the dependency check
            val statuses = deriveMemoized(tasks, stateDeriver)

            // 2. Termination checks
            if (statuses.values.all { it == DONE }) {
                notifier.notify(RunCompleted(tasks))
                return@coroutineScope
            }
            val hasMakingProgress = statuses.values.any { it == IN_PROGRESS || it == PENDING }
            if (!hasMakingProgress) {
                val blocked = tasks.filter { statuses[it] == BLOCKED }
                val failed = tasks.filter { statuses[it] == FAILED }
                notifier.notify(RunDeadlocked(blocked, failed))
                return@coroutineScope
            }

            // 3. Launch agents
            val freeSlots = config.agentPoolSize - activeTaskIds.size
            statuses.entries
                .filter { (task, status) -> (status == PENDING || status == IN_PROGRESS) && task.id !in activeTaskIds }
                .sortedByDescending { (task, _) -> dependencyGraph.downstreamCount(task.id) }
                .take(freeSlots)
                .forEach { (task, _) ->
                    val worktree = worktreeManager.getOrCreate(task.id)
                    activeTaskIds += task.id
                    launch(Dispatchers.IO) {
                        try {
                            val result = agentRunner.run(task, worktree)
                            if (result is AgentResult.Failed) notifier.notify(TaskFailed(task, result.reason))
                        } finally {
                            activeTaskIds -= task.id
                        }
                    }
                }

            // 4. Poll interval
            delay(config.pollIntervalSeconds * 1_000L)
        }
    }
}
```

- `private suspend fun deriveMemoized(tasks: List<Task>, deriver: StateDeriver): Map<Task, TaskStatus>`: builds a map by calling `deriver.statusOf` for each task. Since `StateDeriver` rules 6 and 7 require checking dependency statuses, the memoized map is built in topological order (leaves first) so dependency statuses are already in the map when checking downstream tasks.
- `status()`: calls `deriveMemoized` and returns the result.

**Dependencies:** P6-T1, P5-T3, P5-T5, P4-T1, Phase 7 (AgentRunner — interface only)
**Unit test task:** P6-T3

---

### P6-T3: DefaultOrchestrator Unit Tests

**Files to create:**

- `agentic/src/test/kotlin/com/cramsan/agentic/coordination/DefaultOrchestratorTest.kt`

**What to implement:**

- MockK mocks for `TaskStore`, `StateDeriver`, `DependencyGraph`, `WorktreeManager`, `AgentRunner`, `Notifier`.
- Test termination: all tasks DONE on first tick → `notifier.notify(RunCompleted(...))` called, run returns.
- Test deadlock: no tasks are IN_PROGRESS or PENDING → `notifier.notify(RunDeadlocked(...))` called.
- Test agent launch: one PENDING task with a free slot → `worktreeManager.getOrCreate` and `agentRunner.run` called.
- Test pool sizing: agentPoolSize=2, three PENDING tasks → only 2 agents launched per tick.
- Test priority ordering: two PENDING tasks, task A has `downstreamCount=2`, task B has `downstreamCount=0` → task A is assigned first.
- Test `AgentResult.Failed` causes `notifier.notify(TaskFailed(...))`.
- Use `runCoroutineTest` and advance time using `TestCoroutineScheduler`.

**Dependencies:** P6-T2
**Unit test task:** Self-contained

---

## Phase 7 — Execution Layer

### P7-T1: WorktreeManager Interface and DefaultWorktreeManager

**Files to create:**

- `agentic/src/main/kotlin/com/cramsan/agentic/execution/WorktreeManager.kt`
- `agentic/src/main/kotlin/com/cramsan/agentic/execution/DefaultWorktreeManager.kt`

**What to implement:**

`WorktreeManager.kt`:
- `interface WorktreeManager { fun getOrCreate(taskId: String): Worktree; fun get(taskId: String): Worktree?; fun listAll(): List<Worktree>; fun delete(taskId: String) }`
- `data class Worktree(val taskId: String, val path: java.nio.file.Path, val branchName: String)`

`DefaultWorktreeManager.kt`:
- `class DefaultWorktreeManager(private val repoRoot: java.nio.file.Path, private val agenticDir: java.nio.file.Path, private val baseBranch: String, private val shell: ShellRunner) : WorktreeManager`
- `getOrCreate(taskId)`:
  1. Compute `worktreePath = agenticDir.resolve("worktrees/$taskId")`.
  2. If `worktreePath` exists as a directory, return `Worktree(taskId, worktreePath, "agentic/$taskId")`.
  3. Otherwise: `shell.run("git", "worktree", "add", "-b", "agentic/$taskId", worktreePath.toString(), baseBranch)` from `repoRoot`. Return the `Worktree`.
- `get(taskId)`: checks if `agenticDir.resolve("worktrees/$taskId")` exists; returns `Worktree` if yes, null if not.
- `listAll()`: lists subdirectories of `agenticDir/worktrees/` and maps each to a `Worktree`.
- `delete(taskId)`: runs `shell.run("git", "worktree", "remove", "--force", worktreePath.toString())` then removes the directory if it still exists.

**Dependencies:** P3-T3 (ShellRunner — can be moved to a shared location if needed)
**Unit test task:** P7-T2

---

### P7-T2: WorktreeManager Unit Tests

**Files to create:**

- `agentic/src/test/kotlin/com/cramsan/agentic/execution/DefaultWorktreeManagerTest.kt`

**What to implement:**

- Mock `ShellRunner` using MockK.
- `@TempDir` for `agenticDir`.
- Test `getOrCreate`: when worktree dir does not exist, verify `git worktree add` shell command is called with correct args.
- Test `getOrCreate` idempotency: when worktree dir already exists, verify `git worktree add` is NOT called again.
- Test `get`: returns null when directory does not exist, returns `Worktree` when it does.
- Test `listAll`: create two subdirectories in `agenticDir/worktrees/`, verify both are returned.
- Test `delete`: verify `git worktree remove` shell command is called.

**Dependencies:** P7-T1
**Unit test task:** Self-contained

---

### P7-T3: AgentRunner Interface and DefaultAgentRunner

**Files to create:**

- `agentic/src/main/kotlin/com/cramsan/agentic/execution/AgentRunner.kt`
- `agentic/src/main/kotlin/com/cramsan/agentic/execution/DefaultAgentRunner.kt`

**What to implement:**

`AgentRunner.kt`:
- `interface AgentRunner { suspend fun run(task: Task, worktree: Worktree): AgentResult }`
- `sealed class AgentResult { data class PrOpened(val prId: String, val prUrl: String) : AgentResult(); data class Failed(val reason: String) : AgentResult() }`

`DefaultAgentRunner.kt`:
- `class DefaultAgentRunner(private val agentSession: AgentSession, private val vcsProvider: VcsProvider, private val reviewerAgents: List<ReviewerAgent>, private val reviewerLoader: ReviewerLoader, private val worktreeManager: WorktreeManager, private val agenticDir: java.nio.file.Path) : AgentRunner`

- `run(task, worktree): AgentResult`:
  1. Wraps `agentSession.execute(task, worktree)` in `withTimeout(task.timeoutSeconds * 1_000L)`.
  2. On `TimeoutCancellationException`: writes `agenticDir/tasks/${task.id}/failed.txt` with message "Agent exceeded timeout of ${task.timeoutSeconds}s". Returns `AgentResult.Failed(reason)`.
  3. On `AgentResult.PrOpened`: loads reviewer definitions, runs all reviewer agents in parallel against the PR diff (fetched via `vcsProvider`), posts each reviewer's feedback as a PR comment using the `<!-- agentic-reviewer: {name} -->` format. Returns the `PrOpened` result.
  4. On `AgentResult.Failed`: writes `failed.txt` with the reason. Returns `Failed`.
  5. Wraps the entire block in try/catch for unexpected exceptions: writes `failed.txt` with exception message.

- `private fun writeFailedMarker(taskId: String, reason: String)`: creates parent dirs and writes the reason to `agenticDir/tasks/$taskId/failed.txt`.

- `private fun formatReviewerComment(feedback: ReviewerFeedback): String`: returns the comment string with `<!-- agentic-reviewer: {name} -->` header.

**Dependencies:** P7-T1, Phase 8 (AgentSession), Phase 9 (ReviewerAgent, ReviewerLoader), P3-T1
**Unit test task:** P7-T4

---

### P7-T4: DefaultAgentRunner Unit Tests

**Files to create:**

- `agentic/src/test/kotlin/com/cramsan/agentic/execution/DefaultAgentRunnerTest.kt`

**What to implement:**

- MockK mocks for `AgentSession`, `VcsProvider`, `ReviewerAgent`, `ReviewerLoader`, `WorktreeManager`.
- `@TempDir` for `agenticDir`.
- Test successful flow: `agentSession.execute` returns `PrOpened`, verify reviewer agents are called, verify `addPullRequestComment` is called for each reviewer with the correct format.
- Test timeout: mock `agentSession.execute` to suspend indefinitely, use `runCoroutineTest` with a shortened timeout, verify `failed.txt` is written.
- Test `AgentResult.Failed`: verify `failed.txt` is written with correct reason.
- Test unexpected exception: mock `agentSession.execute` to throw a `RuntimeException`, verify `failed.txt` is written.

**Dependencies:** P7-T3
**Unit test task:** Self-contained

---

## Phase 8 — Claude Integration

### P8-T1: ClaudeClient Interface and KtorClaudeClient

**Files to create:**

- `agentic/src/main/kotlin/com/cramsan/agentic/claude/ClaudeClient.kt`
- `agentic/src/main/kotlin/com/cramsan/agentic/claude/KtorClaudeClient.kt`

**What to implement:**

`ClaudeClient.kt`:
- `interface ClaudeClient { suspend fun chat(model: String, systemPrompt: String, messages: List<ClaudeMessage>, tools: List<ClaudeTool>): ClaudeResponse }`

`KtorClaudeClient.kt`:
- `class KtorClaudeClient(private val httpClient: HttpClient, private val apiKey: String, private val json: Json) : ClaudeClient`
- `chat(...)`:
  1. Constructs a JSON body matching the Anthropic Messages API: `{ "model": ..., "system": ..., "messages": [...], "tools": [...], "max_tokens": 8192 }`.
  2. Posts to `https://api.anthropic.com/v1/messages` with headers `x-api-key: $apiKey` and `anthropic-version: 2023-06-01`.
  3. On HTTP 200: deserializes the response body into `ClaudeResponse`.
  4. On HTTP 429 or 5xx: retries up to 3 times with exponential backoff (1s, 2s, 4s) using a local retry loop.
  5. On other errors or exhausted retries: throws `ClaudeApiException(statusCode, body)`.
- The `HttpClient` is constructed with `ContentNegotiation { json(json) }` installed.

**Dependencies:** P1-T2
**Unit test task:** P8-T2

---

### P8-T2: KtorClaudeClient Unit Tests

**Files to create:**

- `agentic/src/test/kotlin/com/cramsan/agentic/claude/KtorClaudeClientTest.kt`

**What to implement:**

- Use `ktor-client-mock` (`io.ktor:ktor-client-mock:_`) with a `MockEngine` that returns canned responses.
- Test a successful request: verify the request body contains `model`, `system`, `messages`, and `tools` fields. Verify the response is deserialized into a `ClaudeResponse` with the expected `content` blocks.
- Test 429 retry: mock engine returns 429 twice then 200. Verify the method returns successfully after 3 attempts.
- Test persistent 5xx: mock engine always returns 500. Verify `ClaudeApiException` is thrown after 3 attempts.
- Verify correct headers are sent: `x-api-key` and `anthropic-version`.

**Dependencies:** P8-T1
**Unit test task:** Self-contained

---

### P8-T3: Agent Tools and Prompts

**Files to create:**

- `agentic/src/main/kotlin/com/cramsan/agentic/claude/AgentTools.kt`
- `agentic/src/main/kotlin/com/cramsan/agentic/claude/AgentPrompts.kt`

**What to implement:**

`AgentTools.kt` — defines all `ClaudeTool` instances as top-level vals:
- `val READ_FILE_TOOL: ClaudeTool` — schema: `{ path: string }`.
- `val WRITE_FILE_TOOL: ClaudeTool` — schema: `{ path: string, content: string }`.
- `val DELETE_FILE_TOOL: ClaudeTool` — schema: `{ path: string }`.
- `val RUN_COMMAND_TOOL: ClaudeTool` — schema: `{ command: string, workingDir: string? }`.
- `val LIST_FILES_TOOL: ClaudeTool` — schema: `{ glob: string }`.
- `val TASK_COMPLETE_TOOL: ClaudeTool` — schema: `{ prTitle: string, prBody: string }`.
- `val TASK_FAILED_TOOL: ClaudeTool` — schema: `{ reason: string }`.
- `val PROPOSE_AMENDMENT_TOOL: ClaudeTool` — schema: `{ documentType: string, proposedChange: string, isCritical: boolean }`.
- `val SPLIT_TASK_TOOL: ClaudeTool` — schema: `{ currentPrTitle: string, currentPrBody: string, newTaskTitle: string, newTaskDescription: string }`.
- `val ALL_AGENT_TOOLS: List<ClaudeTool>` — convenience list of all above.

Each tool's `inputSchema` is constructed as a `JsonObject` built from `buildJsonObject { ... }`.

`AgentPrompts.kt` — defines prompt-building functions:
- `fun buildTaskStartPrompt(task: Task, documents: List<Pair<String, String>>): String` — where the second element of each pair is the file content.
- `fun buildPrOpenedPrompt(task: Task, gitDiff: String, prDescription: String): String`.
- `fun buildChangesRequestedPrompt(task: Task, gitDiff: String, reviewComments: List<PullRequestComment>): String`.
- `fun buildResumeFromWorktreePrompt(task: Task, gitDiff: String): String`.

Each function produces a multi-line system prompt string as specified in TECH_DESIGN.md §7.3.

**Dependencies:** P1-T2, P8-T1
**Unit test task:** P8-T4

---

### P8-T4: AgentTools Unit Tests

**Files to create:**

- `agentic/src/test/kotlin/com/cramsan/agentic/claude/AgentToolsTest.kt`

**What to implement:**

- Verify each tool has a non-blank `name`, `description`, and `inputSchema`.
- Verify `ALL_AGENT_TOOLS` contains all 9 tools.
- Verify tool name strings match the expected constant strings (e.g., `"read_file"`, `"task_complete"`) — these are the strings Claude uses to signal tool invocations.
- No mocking needed.

**Dependencies:** P8-T3
**Unit test task:** Self-contained

---

### P8-T5: DefaultAgentSession

**Files to create:**

- `agentic/src/main/kotlin/com/cramsan/agentic/claude/DefaultAgentSession.kt`

**What to implement:**

`interface AgentSession { suspend fun execute(task: Task, worktree: Worktree): AgentResult }` (add to `ClaudeClient.kt` or a separate file)

`class DefaultAgentSession(private val claudeClient: ClaudeClient, private val vcsProvider: VcsProvider, private val shell: ShellRunner, private val model: String, private val baseBranch: String, private val documentStore: DocumentStore) : AgentSession`

- `execute(task, worktree)`:
  1. Determine the current context milestone by inspecting the worktree:
     - If `vcsProvider.listOpenPullRequests(...)` has a PR for `agentic/${task.id}` with changes requested → build `buildChangesRequestedPrompt`.
     - If open PR exists but no changes requested → this case means the agent was killed during IN_REVIEW. Re-enter the loop using `buildPrOpenedPrompt`.
     - If worktree has git diff against base → `buildResumeFromWorktreePrompt`.
     - Otherwise → `buildTaskStartPrompt` (fresh start).
  2. Run the agent loop: send messages to `claudeClient.chat(model, systemPrompt, messages, ALL_AGENT_TOOLS)`. Maintain a local `messages: MutableList<ClaudeMessage>` for multi-turn.
  3. On each response, parse `ClaudeResponse.content` for `ToolUse` blocks and dispatch:
     - `read_file`: reads the file at `worktree.path.resolve(input["path"])`, appends result as a user message.
     - `write_file`: writes content to `worktree.path.resolve(input["path"])`.
     - `delete_file`: deletes the file.
     - `run_command`: runs the command in `worktree.path` using `shell.run(...)`, appends stdout/stderr as a user message.
     - `list_files`: lists files matching the glob using `java.nio.file.Files.walk` + pattern matching.
     - `task_complete`: calls `vcsProvider.createPullRequest(...)` with branch `agentic/${task.id}`, resets the context (clears `messages`), returns `AgentResult.PrOpened(pr.id, pr.url)`.
     - `task_failed`: returns `AgentResult.Failed(reason)`.
     - `propose_amendment`: creates a Document PR via `vcsProvider.createPullRequest(...)` with label `agentic-document`. If `isCritical=true`, writes `agenticDir/tasks/${task.id}/awaiting-amendment.txt` and polls `vcsProvider.isPullRequestMerged(amendmentPrId)` in a loop with 30s sleep until merged, then clears the marker file and continues.
     - `split_task`: creates a Code PR for current work and a Document PR for the new task, then returns `PrOpened`.
  4. If `stopReason == "end_turn"` and no terminal tool was invoked, append an assistant message and send a continuation prompt.
  5. Context reset at each milestone: replace `messages` list with a fresh `listOf(ClaudeMessage("user", newSystemPromptContent))`.

**Dependencies:** P8-T3, P3-T1, P2-T1, P1-T2
**Unit test task:** P8-T6

---

### P8-T6: DefaultAgentSession Unit Tests

**Files to create:**

- `agentic/src/test/kotlin/com/cramsan/agentic/claude/DefaultAgentSessionTest.kt`

**What to implement:**

- MockK mocks for `ClaudeClient`, `VcsProvider`, `ShellRunner`, `DocumentStore`.
- Test `task_complete` tool response: mock `claudeClient.chat` to return a response containing a `ToolUse("task_complete", ...)` block, mock `vcsProvider.createPullRequest` to return a `PullRequest`. Assert `execute` returns `AgentResult.PrOpened`.
- Test `task_failed` tool response: mock response with `task_failed`. Assert `execute` returns `AgentResult.Failed`.
- Test `read_file` tool: mock response with `read_file` on first turn, then `task_complete` on second turn. Assert the file content was appended to messages. Use `@TempDir` for the worktree path with a pre-written file.
- Test `run_command` tool: mock `shell.run` to return a `ShellResult`, assert the stdout is appended as a message.
- Test critical `propose_amendment`: mock `vcsProvider.isPullRequestMerged` to return false on first call, true on second. Assert the session polls and eventually continues.
- Use `runCoroutineTest`.

**Dependencies:** P8-T5
**Unit test task:** Self-contained

---

## Phase 9 — Reviewer Agents

### P9-T1: ReviewerAgent and ReviewerLoader Interfaces

**Files to create:**

- `agentic/src/main/kotlin/com/cramsan/agentic/reviewer/ReviewerLoader.kt`
- `agentic/src/main/kotlin/com/cramsan/agentic/reviewer/ReviewerAgent.kt`

**What to implement:**

`ReviewerLoader.kt`:
- `interface ReviewerLoader { fun loadAll(): List<ReviewerDefinition> }`

`ReviewerAgent.kt`:
- `interface ReviewerAgent { suspend fun reviewDocuments(reviewer: ReviewerDefinition, documents: List<AgenticDocument>): ReviewerFeedback; suspend fun reviewCode(reviewer: ReviewerDefinition, task: Task, diff: String): ReviewerFeedback }`

**Dependencies:** P1-T2
**Unit test task:** N/A — interfaces only

---

### P9-T2: FileSystemReviewerLoader

**Files to create:**

- `agentic/src/main/kotlin/com/cramsan/agentic/reviewer/FileSystemReviewerLoader.kt`

**What to implement:**

`class FileSystemReviewerLoader(private val reviewersDir: java.nio.file.Path) : ReviewerLoader`

- `loadAll()`:
  1. If `reviewersDir` does not exist, return `emptyList()`.
  2. Lists all `.md` files in `reviewersDir` (non-recursive).
  3. For each file: reads the full content as `systemPrompt`, derives `name` from the filename without extension (e.g., `security.md` → `"security"`).
  4. Returns `List<ReviewerDefinition>`.
  5. Logs count at INFO level.

**Dependencies:** P9-T1, P1-T2
**Unit test task:** P9-T3

---

### P9-T3: FileSystemReviewerLoader Unit Tests

**Files to create:**

- `agentic/src/test/kotlin/com/cramsan/agentic/reviewer/FileSystemReviewerLoaderTest.kt`

**What to implement:**

- `@TempDir` with synthetic `.md` files.
- Test `loadAll()` returns one `ReviewerDefinition` per `.md` file.
- Test that the `name` field is the filename without the `.md` extension.
- Test that the `systemPrompt` field contains the full file content.
- Test that an empty directory returns an empty list.
- Test that a non-existent directory returns an empty list.

**Dependencies:** P9-T2
**Unit test task:** Self-contained

---

### P9-T4: FakeReviewerAgent

**Files to create:**

- `agentic/src/main/kotlin/com/cramsan/agentic/reviewer/fake/FakeReviewerAgent.kt`

**What to implement:**

`class FakeReviewerAgent(private val documentFeedback: String = "No issues found.", private val codeFeedback: String = "LGTM.") : ReviewerAgent`

- `reviewDocuments(reviewer, documents)`: returns `ReviewerFeedback(reviewer.name, documentFeedback)`.
- `reviewCode(reviewer, task, diff)`: returns `ReviewerFeedback(reviewer.name, codeFeedback)`.

**Dependencies:** P9-T1
**Unit test task:** Exercised via integration tests

---

### P9-T5: ClaudeReviewerAgent

**Files to create:**

- `agentic/src/main/kotlin/com/cramsan/agentic/reviewer/claude/ClaudeReviewerAgent.kt`

**What to implement:**

`class ClaudeReviewerAgent(private val claudeClient: ClaudeClient, private val model: String) : ReviewerAgent`

- `reviewDocuments(reviewer, documents)`:
  1. Builds a user message with the full content of all documents concatenated with clear section headers.
  2. Calls `claudeClient.chat(model, systemPrompt = reviewer.systemPrompt, messages = listOf(userMessage), tools = emptyList())`.
  3. Extracts the first `Text` content block from the response.
  4. Returns `ReviewerFeedback(reviewer.name, responseText)`.

- `reviewCode(reviewer, task, diff)`:
  1. Builds a user message: "Task: ${task.title}\n\nDiff:\n```\n$diff\n```".
  2. Calls `claudeClient.chat(...)` with no tools.
  3. Returns `ReviewerFeedback(reviewer.name, responseText)`.

**Dependencies:** P9-T1, P8-T1
**Unit test task:** P9-T6

---

### P9-T6: ClaudeReviewerAgent Unit Tests

**Files to create:**

- `agentic/src/test/kotlin/com/cramsan/agentic/reviewer/claude/ClaudeReviewerAgentTest.kt`

**What to implement:**

- MockK mock for `ClaudeClient`.
- Test `reviewDocuments`: verify `claudeClient.chat` is called with `tools = emptyList()` and `systemPrompt = reviewer.systemPrompt`. Assert the returned `ReviewerFeedback.content` matches the mock response text.
- Test `reviewCode`: verify the diff is included in the message, verify the reviewer name is passed through correctly.
- Verify that no tool calls are made (tools list is empty).

**Dependencies:** P9-T5
**Unit test task:** Self-contained

---

## Phase 10 — CLI and Dependency Injection

### P10-T1: Koin Module Definitions

**Files to create:**

- `agentic/src/main/kotlin/com/cramsan/agentic/app/AgenticModule.kt`

**What to implement:**

`AgenticModule.kt` — a Koin `module { }` block that wires together all concrete implementations:

- `single<Json> { Json { ignoreUnknownKeys = true; isLenient = true } }`
- `single<ShellRunner> { ShellRunner() }`
- `single<AgenticConfig> { /* read from configPath parameter */ }` — reads and parses `config.json` from the path passed as a Koin parameter.
- `single<VcsProvider> { val config = get<AgenticConfig>(); when (config.vcsProvider) { is GitHub -> GitHubVcsProvider(owner, repo, get(), get()) } }`
- `single<WorktreeManager> { DefaultWorktreeManager(repoRoot, agenticDir, get<AgenticConfig>().baseBranch, get()) }`
- `single<TaskStore> { FileSystemTaskStore(agenticDir.resolve("docs/task-list.md")) }`
- `single<DocumentStore> { FileSystemDocumentStore(agenticDir.resolve("docs"), get()) }`
- `single<DependencyGraph> { DefaultDependencyGraph(get<TaskStore>().getAll()) }`
- `single<StateDeriver> { DefaultStateDeriver(get(), get(), agenticDir) }`
- `single<Notifier> { VcsCommentNotifier(get()) }`
- `single<ReviewerLoader> { FileSystemReviewerLoader(agenticDir.resolve("docs/reviewers")) }`
- `single<ReviewerAgent> { ClaudeReviewerAgent(get(), get<AgenticConfig>().claudeModel) }`
- `single<ClaudeClient> { KtorClaudeClient(get(), get<AgenticConfig>().let { System.getenv(it.anthropicApiKeyEnvVar) ?: error("API key env var not set") }, get()) }`
- `single<AgentSession> { DefaultAgentSession(get(), get(), get(), get<AgenticConfig>().claudeModel, get<AgenticConfig>().baseBranch, get()) }`
- `single<AgentRunner> { DefaultAgentRunner(get(), get(), listOf(get()), get(), get(), agenticDir) }`
- `single<Orchestrator> { DefaultOrchestrator(get(), get(), get(), get(), get(), get()) }`
- `single<Scaffolder> { DefaultScaffolder() }`
- `single<ValidationService> { DefaultValidationService(get(), get(), get<AgenticConfig>().claudeModel, listOf(get()), get(), get()) }`

The module receives `agenticDir: Path` and `repoRoot: Path` as constructor parameters, not via Koin scope.

**Dependencies:** All prior phases
**Unit test task:** N/A — wiring only; validated by integration tests

---

### P10-T2: CLI Commands

**Files to create:**

- `agentic/src/main/kotlin/com/cramsan/agentic/app/Main.kt`
- `agentic/src/main/kotlin/com/cramsan/agentic/app/AgenticCli.kt`
- `agentic/src/main/kotlin/com/cramsan/agentic/app/commands/InitCommand.kt`
- `agentic/src/main/kotlin/com/cramsan/agentic/app/commands/ValidateCommand.kt`
- `agentic/src/main/kotlin/com/cramsan/agentic/app/commands/StartCommand.kt`
- `agentic/src/main/kotlin/com/cramsan/agentic/app/commands/ResumeCommand.kt`
- `agentic/src/main/kotlin/com/cramsan/agentic/app/commands/StatusCommand.kt`
- `agentic/src/main/kotlin/com/cramsan/agentic/app/commands/TaskCommand.kt`

**What to implement:**

`Main.kt`:
- `fun main(args: Array<String>) = AgenticCli().main(args)`

`AgenticCli.kt`:
- `class AgenticCli : CliktCommand(name = "agentic", invokeWithoutSubcommand = false)`
- Global options declared with `by option(...)`:
  - `--config <path>` defaulting to `.agentic/config.json`
  - `--log-level <level>` with choices `debug`, `info`, `warn`, `error`, defaulting to `info`
- Subcommands registered in `init { addCommand(InitCommand()); addCommand(ValidateCommand()); ... }`

`InitCommand.kt`:
- `class InitCommand : CliktCommand(name = "init", help = "Scaffold input docs and write default config.json")`
- `run()`: bootstraps a minimal Koin context (no Claude/VCS dependencies), calls `scaffolder.scaffold(outputDir)`, writes a default `config.json` using `Json.encodeToString(AgenticConfig(...))`.

`ValidateCommand.kt`:
- `class ValidateCommand : CliktCommand(name = "validate", help = "Run a validation pass, print findings, and exit")`
- Options: none additional (uses global `--config`).
- `run()`: boots Koin with `AgenticModule`, calls `runBlocking { validationService.runValidationPass() }`. Prints the report summary. Exits with `exitProcess(1)` if any BLOCKING issues remain; otherwise exits 0.

`StartCommand.kt` and `ResumeCommand.kt`:
- Both `class StartCommand : CliktCommand(name = "start")` and `class ResumeCommand : CliktCommand(name = "resume")` are functionally identical (per TECH_DESIGN.md §8: state is always re-derived).
- Options: `--agents <N>` (overrides `agentPoolSize`), `--dry-run` (prints task assignment order without running agents).
- `run()`: boots Koin, builds `OrchestratorConfig` from `AgenticConfig` (overriding `agentPoolSize` if `--agents` supplied). If `--dry-run`: calls `orchestrator.status()`, prints results, exits. Otherwise: calls `runBlocking { orchestrator.run(config) }`.

`StatusCommand.kt`:
- `run()`: calls `orchestrator.status()`, prints a table with columns `Task ID`, `Title`, `Status`.

`TaskCommand.kt`:
- Parent `class TaskCommand : CliktCommand(name = "task")` with subcommands:
  - `ListSubcommand`: prints all tasks with status (delegates to `orchestrator.status()`).
  - `ShowSubcommand(id: String)`: prints full task detail — title, description, status, PR URL (fetched from `vcsProvider`), failure reason from `failed.txt` if present.
  - `RetrySubcommand(id: String)`: deletes `agenticDir/tasks/{id}/failed.txt` (allowing StateDeriver to re-evaluate from worktree presence).
  - `UnblockSubcommand(id: String)`: writes a special `unblocked.txt` marker that `StateDeriver` checks before returning BLOCKED — if present, treat as PENDING for one poll cycle then delete.

**Dependencies:** P10-T1, All prior phases
**Unit test task:** P10-T3

---

### P10-T3: CLI Command Unit Tests

**Files to create:**

- `agentic/src/test/kotlin/com/cramsan/agentic/app/commands/ValidateCommandTest.kt`
- `agentic/src/test/kotlin/com/cramsan/agentic/app/commands/StartCommandTest.kt`

**What to implement:**

- Use Clikt's `CliktCommand.test(...)` API to invoke commands without `main()`.
- `ValidateCommandTest`: mock `ValidationService` with MockK, inject via a test-specific Koin module. Assert exit code 0 when no blocking issues, exit code 1 when blocking issues exist. Assert output contains issue descriptions.
- `StartCommandTest`: mock `Orchestrator` with MockK. Assert `orchestrator.run(...)` is called with the correct `OrchestratorConfig`. Test `--dry-run` flag calls `orchestrator.status()` instead.

**Dependencies:** P10-T2
**Unit test task:** Self-contained

---

## Phase 11 — Integration Tests

### P11-T1: Input Layer Integration Test

**Files to create:**

- `agentic/src/integTest/kotlin/com/cramsan/agentic/input/ValidationIntegrationTest.kt`

**What to implement:**

Scope: real filesystem, real `DefaultScaffolder`, real `FileSystemDocumentStore`, real `FileSystemReviewerLoader`, mocked `ClaudeClient` (using a pre-recorded response fixture), `FakeReviewerAgent`.

Test class setup:
- `@TempDir` JUnit5 annotation for an isolated directory.
- In `@BeforeEach`: call `DefaultScaffolder().scaffold(tempDir)` to generate all document files. Construct `FileSystemDocumentStore(tempDir, json)`.
- Provide a mock `ClaudeClient` that returns a canned `ClaudeResponse` with a `Text` block containing a JSON array of `ValidationIssue` objects (fixture defined as a constant in the test class).

Test cases:
- **Happy path**: canned response contains no BLOCKING issues. Assert `allValidated()` returns true after `runValidationPass()`. Assert the `validation-report.md` file is written.
- **Blocking issue flow**: canned response contains one BLOCKING issue. Assert at least one document has status `NEEDS_REVISION`. Assert `allValidated()` returns false.
- **`onDocumentChanged()` resets all**: after validating, write a file change, call `onDocumentChanged()`, assert all statuses are back to `UNREVIEWED`.
- **Reviewer agents run in parallel**: provide two `FakeReviewerAgent` instances. Assert both produce `ReviewerFeedback` entries in the output.

**Dependencies:** All Phase 2 tasks, P8-T1, P9-T4
**Unit test task:** Self-contained (integration test)

---

### P11-T2: Orchestrator Lifecycle Integration Test

**Files to create:**

- `agentic/src/integTest/kotlin/com/cramsan/agentic/coordination/OrchestratorLifecycleIntegrationTest.kt`

**What to implement:**

Scope: real filesystem, real git (using a temporary git repo), `FakeVcsProvider`, `FakeNotifier`, `DefaultWorktreeManager` (calls real `git worktree`), mocked `AgentSession`.

Test class setup:
- `@TempDir` for the git repo root.
- `@BeforeEach`: initializes a real git repo (`git init`, `git commit --allow-empty -m "init"`), constructs all real coordination-layer classes, uses `FakeVcsProvider` and `FakeNotifier`.
- Provides a task list with 2 tasks (task-B depends on task-A) written to a temp `task-list.md`.

Test cases:
- **Full lifecycle — linear dependency**: mock `AgentSession.execute` for task-A to return `PrOpened`. On next poll, call `fakeVcsProvider.mergePullRequest(prId)`. Assert that on the subsequent tick, task-B becomes PENDING and an agent is launched. Mock task-B to also return `PrOpened`, then merge. Assert `FakeNotifier.receivedEvents` contains `RunCompleted`.
- **Deadlock detection**: mock `AgentSession.execute` for task-A to return `Failed`. Assert `FakeNotifier` receives `RunDeadlocked` since task-B becomes BLOCKED.
- **Crash-resume**: run the orchestrator until task-A's agent is launched and the worktree directory exists. Cancel the coroutine scope (simulating a crash). Construct a fresh `DefaultOrchestrator` with fresh `FakeVcsProvider` state. On the new run, assert `StateDeriver` returns IN_PROGRESS for task-A (worktree exists, no PR), and the new agent is launched against the existing worktree.

**Dependencies:** All Phase 5, 6, 7 tasks, P4-T2
**Unit test task:** Self-contained (integration test)

---

### P11-T3: ShellRunner and WorktreeManager Integration Test

**Files to create:**

- `agentic/src/integTest/kotlin/com/cramsan/agentic/execution/WorktreeManagerIntegrationTest.kt`

**What to implement:**

Scope: real `git` binary, real `ShellRunner`, real `DefaultWorktreeManager`, temporary git repo.

Test cases:
- `getOrCreate` creates a git worktree directory at the expected path.
- `getOrCreate` is idempotent: calling it twice for the same task ID does not fail.
- `listAll` returns the correct number of worktrees.
- `delete` removes the worktree directory and the git tracking entry.

Note: this test requires `git` to be installed on the test machine. The test should be tagged appropriately (e.g., `@Tag("requires-git")`) and the CI pipeline should ensure git is available.

**Dependencies:** P7-T1, P7-T2
**Unit test task:** Self-contained (integration test)

---

## Phase 12 — E2E Test Scaffolding

### P12-T1: E2E Test Infrastructure

**Files to create:**

- `agentic/src/integTest/kotlin/com/cramsan/agentic/e2e/E2EAnnotation.kt`
- `agentic/src/integTest/kotlin/com/cramsan/agentic/e2e/E2ETestBase.kt`

**What to implement:**

`E2EAnnotation.kt`:
- `@Target(AnnotationTarget.CLASS) @Retention(AnnotationRetention.RUNTIME) @Tag("E2E") annotation class E2ETest`

`E2ETestBase.kt`:
- Abstract base class providing:
  - `protected val anthropicApiKey: String` — read from `System.getenv("ANTHROPIC_API_KEY")`. If blank, the `@BeforeAll` method calls `assumeTrue(false, "ANTHROPIC_API_KEY not set, skipping E2E tests")` to skip gracefully.
  - `protected val githubToken: String` — same pattern for `GITHUB_TOKEN`.
  - A `@TempDir` for the test repo root.
  - Helper `fun initGitRepo(dir: Path)` that runs `git init`, configures user email and name, and makes an initial empty commit.
  - Helper `fun writeTaskList(dir: Path, tasks: List<Task>)` that writes a synthetic `task-list.md` in the expected format.

`build.gradle.kts` — modify the `integTest` task to exclude `@Tag("E2E")` tests from the standard build:
```
tasks.register<Test>("integTest") {
    // ... existing config ...
    useJUnitPlatform {
        excludeTags("E2E")
    }
}
tasks.register<Test>("e2eTest") {
    testClassesDirs = sourceSets["integTest"].output.classesDirs
    classpath = sourceSets["integTest"].runtimeClasspath
    useJUnitPlatform { includeTags("E2E") }
}
```

**Dependencies:** All prior phases, P1-T1
**Unit test task:** Self-contained (infrastructure only)

---

### P12-T2: Smoke E2E Test

**Files to create:**

- `agentic/src/integTest/kotlin/com/cramsan/agentic/e2e/SmokE2ETest.kt`

**What to implement:**

`@E2ETest class SmokeE2ETest : E2ETestBase()`

- Test: `trivialTask_completesSuccessfully`:
  1. Creates a real git repo in `@TempDir`.
  2. Writes a minimal `task-list.md` with a single trivial task: "Create a file named hello.txt with the content 'hello world'."
  3. Boots the full Koin module with real `ClaudeClient` (using `ANTHROPIC_API_KEY`), real `GitHubVcsProvider` (requires `GITHUB_TOKEN` and a test repo), real `DefaultWorktreeManager`.
  4. Calls `orchestrator.run(config)` with `agentPoolSize = 1`.
  5. Asserts that the `FakeNotifier` (or a console-capturing notifier) receives `RunCompleted`.
  6. Asserts that `hello.txt` was created in the worktree.

This test serves as a scaffold — it is expected to fail until Claude API and `gh` CLI integration is fully working. It documents the expected E2E contract.

**Dependencies:** P12-T1, All prior phases
**Unit test task:** Self-contained (E2E test)

---

## Dependency and Sequencing Summary

| Phase | Blocking Dependencies |
|---|---|
| Phase 1 | None |
| Phase 2 | Phase 1 (interfaces can start; implementations need P1-T2) |
| Phase 3 | Phase 1 |
| Phase 4 | Phase 1, Phase 3 (interface) |
| Phase 5 | Phase 1, Phase 3 (interface), Phase 7 (interface only — declare, do not implement) |
| Phase 6 | Phase 5, Phase 4, Phase 7 (interface) |
| Phase 7 | Phase 3 (ShellRunner), Phase 8 (AgentSession interface), Phase 9 (interfaces) |
| Phase 8 | Phase 1, Phase 2 (interfaces), Phase 3 (interface) |
| Phase 9 | Phase 1, Phase 8 (ClaudeClient) |
| Phase 10 | All prior phases |
| Phase 11 | All prior phases |
| Phase 12 | All prior phases |

The critical path for getting a compilable module is: P1-T1 → P1-T2 → P3-T1 → P5-T1 → P6-T1 → P7-T1. Everything else can develop in parallel once the interfaces from each phase exist.

---

### Critical Files for Implementation

- `/home/cramsan/git/MonoRepo/agentic/build.gradle.kts`
- `/home/cramsan/git/MonoRepo/settings.gradle.kts`
- `/home/cramsan/git/MonoRepo/build.gradle.kts`
- `/home/cramsan/git/MonoRepo/versions.properties`
- `/home/cramsan/git/MonoRepo/agentic/docs/TECH_DESIGN.md`

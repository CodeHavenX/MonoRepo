package com.cramsan.agentic.coordination

import com.cramsan.agentic.core.PullRequest
import com.cramsan.agentic.core.PullRequestState
import com.cramsan.agentic.core.Task
import com.cramsan.agentic.core.TaskStatus
import com.cramsan.agentic.execution.Worktree
import com.cramsan.agentic.execution.WorktreeManager
import com.cramsan.agentic.vcs.VcsProvider
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.assertEquals

/**
 * Extended tests for DefaultStateDeriver enforcing the full derivation-order contract
 * specified in TECH_DESIGN.md §5.2.
 */
class DefaultStateDeriverExtendedTest {

    @TempDir
    lateinit var agenticDir: Path

    private val vcsProvider = mockk<VcsProvider>()
    private val worktreeManager = mockk<WorktreeManager>()
    private lateinit var deriver: DefaultStateDeriver

    private val task = Task(id = "task-001", title = "My Task", description = "Desc", dependencies = emptyList())
    private val depTask = Task(id = "dep-task", title = "Dep", description = "Dep", dependencies = emptyList())

    private fun makeMergedPr(sourceBranch: String) = PullRequest(
        id = "pr-1", url = "url", title = "PR", state = PullRequestState.MERGED,
        sourceBranch = sourceBranch, targetBranch = "main", labels = listOf("agentic-code"),
    )

    private fun makeOpenPr(sourceBranch: String) = PullRequest(
        id = "pr-1", url = "url", title = "PR", state = PullRequestState.OPEN,
        sourceBranch = sourceBranch, targetBranch = "main", labels = listOf("agentic-code"),
    )

    private fun makeContext(
        merged: List<PullRequest> = emptyList(),
        open: List<PullRequest> = emptyList(),
    ) = PrContext(mergedPrs = merged, openPrs = open)

    @BeforeEach
    fun setup() {
        deriver = DefaultStateDeriver(vcsProvider, worktreeManager, agenticDir)
        coEvery { worktreeManager.get(any()) } returns null
    }

    // ── Derivation precedence: merged PR beats everything ────────────────────

    @Test
    fun `merged PR beats failed_txt — DONE takes priority`() = runTest {
        val failedFile = agenticDir.resolve("tasks/task-001/failed.txt")
        Files.createDirectories(failedFile.parent)
        Files.writeString(failedFile, "previous failure before merge")
        val ctx = makeContext(merged = listOf(makeMergedPr("agentic/task-001")))

        assertEquals(TaskStatus.DONE, deriver.statusOf(task, prContext = ctx))
    }

    @Test
    fun `merged PR beats open PR — DONE takes priority`() = runTest {
        coEvery { vcsProvider.pullRequestHasRequestedChanges(any()) } returns false
        val ctx = makeContext(
            merged = listOf(makeMergedPr("agentic/task-001")),
            open = listOf(makeOpenPr("agentic/task-001")),
        )

        assertEquals(TaskStatus.DONE, deriver.statusOf(task, prContext = ctx))
    }

    @Test
    fun `merged PR for different branch does not return DONE`() = runTest {
        val ctx = makeContext(merged = listOf(makeMergedPr("agentic/other-task")))

        assertEquals(TaskStatus.PENDING, deriver.statusOf(task, prContext = ctx))
    }

    // ── failed.txt beats open PR ─────────────────────────────────────────────

    @Test
    fun `failed_txt beats open PR — FAILED takes priority`() = runTest {
        val failedFile = agenticDir.resolve("tasks/task-001/failed.txt")
        Files.createDirectories(failedFile.parent)
        Files.writeString(failedFile, "ran out of tokens")
        coEvery { vcsProvider.pullRequestHasRequestedChanges(any()) } returns false
        val ctx = makeContext(open = listOf(makeOpenPr("agentic/task-001")))

        assertEquals(TaskStatus.FAILED, deriver.statusOf(task, prContext = ctx))
    }

    @Test
    fun `failed_txt beats worktree — FAILED takes priority`() = runTest {
        val failedFile = agenticDir.resolve("tasks/task-001/failed.txt")
        Files.createDirectories(failedFile.parent)
        Files.writeString(failedFile, "reason")
        coEvery { worktreeManager.get("task-001") } returns Worktree("task-001", Path.of("/tmp/wt"), "agentic/task-001")

        assertEquals(TaskStatus.FAILED, deriver.statusOf(task, prContext = makeContext()))
    }

    // ── unblocked.txt: human override ────────────────────────────────────────

    @Test
    fun `unblocked_txt overrides BLOCKED to PENDING when deps not done`() = runTest {
        val taskWithDeps = task.copy(dependencies = listOf("dep-task"))
        val unblockedFile = agenticDir.resolve("tasks/task-001/unblocked.txt")
        Files.createDirectories(unblockedFile.parent)
        Files.writeString(unblockedFile, "manually unblocked by operator")

        val result = deriver.statusOf(taskWithDeps, mapOf("dep-task" to TaskStatus.IN_REVIEW), makeContext())

        assertEquals(TaskStatus.PENDING, result)
    }

    @Test
    fun `unblocked_txt is deleted after use — one-shot behavior`() = runTest {
        val taskWithDeps = task.copy(dependencies = listOf("dep-task"))
        val unblockedFile = agenticDir.resolve("tasks/task-001/unblocked.txt")
        Files.createDirectories(unblockedFile.parent)
        Files.writeString(unblockedFile, "manually unblocked")

        deriver.statusOf(taskWithDeps, mapOf("dep-task" to TaskStatus.IN_REVIEW), makeContext())

        assertEquals(false, Files.exists(unblockedFile))
    }

    @Test
    fun `second call after unblocked_txt deleted returns BLOCKED`() = runTest {
        val taskWithDeps = task.copy(dependencies = listOf("dep-task"))
        val unblockedFile = agenticDir.resolve("tasks/task-001/unblocked.txt")
        Files.createDirectories(unblockedFile.parent)
        Files.writeString(unblockedFile, "manually unblocked")

        deriver.statusOf(taskWithDeps, mapOf("dep-task" to TaskStatus.IN_REVIEW), makeContext())

        val result = deriver.statusOf(taskWithDeps, mapOf("dep-task" to TaskStatus.IN_REVIEW), makeContext())
        assertEquals(TaskStatus.BLOCKED, result)
    }

    @Test
    fun `unblocked_txt does not affect task that is already PENDING`() = runTest {
        val unblockedFile = agenticDir.resolve("tasks/task-001/unblocked.txt")
        Files.createDirectories(unblockedFile.parent)
        Files.writeString(unblockedFile, "should not be read for PENDING task")

        val result = deriver.statusOf(task, prContext = makeContext())

        assertEquals(TaskStatus.PENDING, result)
    }

    // ── Dependency resolution ─────────────────────────────────────────────────

    @Test
    fun `task with deps all DONE returns PENDING`() = runTest {
        val taskWithDeps = task.copy(dependencies = listOf("dep-a", "dep-b"))
        val resolved = mapOf("dep-a" to TaskStatus.DONE, "dep-b" to TaskStatus.DONE)

        assertEquals(TaskStatus.PENDING, deriver.statusOf(taskWithDeps, resolved, makeContext()))
    }

    @Test
    fun `task with dep IN_REVIEW returns BLOCKED — IN_REVIEW is not DONE`() = runTest {
        val taskWithDeps = task.copy(dependencies = listOf("dep-a"))
        assertEquals(TaskStatus.BLOCKED, deriver.statusOf(taskWithDeps, mapOf("dep-a" to TaskStatus.IN_REVIEW), makeContext()))
    }

    @Test
    fun `task with dep IN_PROGRESS returns BLOCKED`() = runTest {
        val taskWithDeps = task.copy(dependencies = listOf("dep-a"))
        assertEquals(TaskStatus.BLOCKED, deriver.statusOf(taskWithDeps, mapOf("dep-a" to TaskStatus.IN_PROGRESS), makeContext()))
    }

    @Test
    fun `task with dep FAILED returns BLOCKED`() = runTest {
        val taskWithDeps = task.copy(dependencies = listOf("dep-a"))
        assertEquals(TaskStatus.BLOCKED, deriver.statusOf(taskWithDeps, mapOf("dep-a" to TaskStatus.FAILED), makeContext()))
    }

    @Test
    fun `task with dep BLOCKED returns BLOCKED`() = runTest {
        val taskWithDeps = task.copy(dependencies = listOf("dep-a"))
        assertEquals(TaskStatus.BLOCKED, deriver.statusOf(taskWithDeps, mapOf("dep-a" to TaskStatus.BLOCKED), makeContext()))
    }

    @Test
    fun `task with mixed deps — one DONE one PENDING — returns BLOCKED`() = runTest {
        val taskWithDeps = task.copy(dependencies = listOf("dep-a", "dep-b"))
        assertEquals(
            TaskStatus.BLOCKED,
            deriver.statusOf(taskWithDeps, mapOf("dep-a" to TaskStatus.DONE, "dep-b" to TaskStatus.PENDING), makeContext()),
        )
    }

    @Test
    fun `task with empty resolvedDependencies falls back to BLOCKED`() = runTest {
        val taskWithDeps = task.copy(dependencies = listOf("dep-a"))
        assertEquals(TaskStatus.BLOCKED, deriver.statusOf(taskWithDeps, emptyMap(), makeContext()))
    }

    // ── PR branch name matching ───────────────────────────────────────────────

    @Test
    fun `open PR for different task does not affect this task`() = runTest {
        coEvery { vcsProvider.pullRequestHasRequestedChanges(any()) } returns false
        val ctx = makeContext(open = listOf(makeOpenPr("agentic/other-task")))

        assertEquals(TaskStatus.PENDING, deriver.statusOf(task, prContext = ctx))
    }

    @Test
    fun `worktree for different task does not affect this task`() = runTest {
        coEvery { worktreeManager.get("task-001") } returns null
        coEvery { worktreeManager.get("other-task") } returns Worktree("other-task", Path.of("/tmp/wt"), "agentic/other-task")

        assertEquals(TaskStatus.PENDING, deriver.statusOf(task, prContext = makeContext()))
    }
}

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

class DefaultStateDeriverTest {

    @TempDir
    lateinit var agenticDir: Path

    private val vcsProvider = mockk<VcsProvider>()
    private val worktreeManager = mockk<WorktreeManager>()
    private lateinit var deriver: DefaultStateDeriver

    private val task = Task(
        id = "task-001",
        title = "My Task",
        description = "Desc",
        dependencies = emptyList(),
    )

    private fun makePr(sourceBranch: String, state: PullRequestState = PullRequestState.OPEN) = PullRequest(
        id = "pr-1",
        url = "https://github.com/owner/repo/pull/1",
        title = "Test PR",
        state = state,
        sourceBranch = sourceBranch,
        targetBranch = "main",
        labels = listOf("agentic-code"),
    )

    @BeforeEach
    fun setup() {
        deriver = DefaultStateDeriver(vcsProvider, worktreeManager, agenticDir)
        coEvery { vcsProvider.listMergedPullRequests(any()) } returns emptyList()
        coEvery { vcsProvider.listOpenPullRequests(any()) } returns emptyList()
        coEvery { worktreeManager.get(any()) } returns null
    }

    @Test
    fun `merged PR returns DONE`() = runTest {
        coEvery { vcsProvider.listMergedPullRequests(any()) } returns listOf(
            makePr("agentic/task-001", PullRequestState.MERGED)
        )
        assertEquals(TaskStatus.DONE, deriver.statusOf(task))
    }

    @Test
    fun `failed_txt exists returns FAILED`() = runTest {
        val failedFile = agenticDir.resolve("tasks/task-001/failed.txt")
        Files.createDirectories(failedFile.parent)
        Files.writeString(failedFile, "timeout")
        assertEquals(TaskStatus.FAILED, deriver.statusOf(task))
    }

    @Test
    fun `open PR with changes requested returns IN_PROGRESS`() = runTest {
        coEvery { vcsProvider.listOpenPullRequests(any()) } returns listOf(makePr("agentic/task-001"))
        coEvery { vcsProvider.pullRequestHasRequestedChanges("pr-1") } returns true
        assertEquals(TaskStatus.IN_PROGRESS, deriver.statusOf(task))
    }

    @Test
    fun `open PR without changes requested returns IN_REVIEW`() = runTest {
        coEvery { vcsProvider.listOpenPullRequests(any()) } returns listOf(makePr("agentic/task-001"))
        coEvery { vcsProvider.pullRequestHasRequestedChanges("pr-1") } returns false
        assertEquals(TaskStatus.IN_REVIEW, deriver.statusOf(task))
    }

    @Test
    fun `worktree exists returns IN_PROGRESS`() = runTest {
        coEvery { worktreeManager.get("task-001") } returns Worktree("task-001", Path.of("/tmp/wt"), "agentic/task-001")
        assertEquals(TaskStatus.IN_PROGRESS, deriver.statusOf(task))
    }

    @Test
    fun `no PR no worktree no deps returns PENDING`() = runTest {
        assertEquals(TaskStatus.PENDING, deriver.statusOf(task))
    }

    @Test
    fun `all deps DONE returns PENDING`() = runTest {
        val taskWithDeps = task.copy(dependencies = listOf("dep-1", "dep-2"))
        val resolved = mapOf("dep-1" to TaskStatus.DONE, "dep-2" to TaskStatus.DONE)
        assertEquals(TaskStatus.PENDING, deriver.statusOf(taskWithDeps, resolved))
    }

    @Test
    fun `one dep not DONE returns BLOCKED`() = runTest {
        val taskWithDeps = task.copy(dependencies = listOf("dep-1", "dep-2"))
        val resolved = mapOf("dep-1" to TaskStatus.DONE, "dep-2" to TaskStatus.IN_REVIEW)
        assertEquals(TaskStatus.BLOCKED, deriver.statusOf(taskWithDeps, resolved))
    }

    @Test
    fun `dep in task dependencies but absent from resolvedDependencies returns BLOCKED`() = runTest {
        val taskWithDeps = task.copy(dependencies = listOf("dep-1", "dep-missing"))
        // dep-missing is not present in the map at all
        val resolved = mapOf("dep-1" to TaskStatus.DONE)
        assertEquals(TaskStatus.BLOCKED, deriver.statusOf(taskWithDeps, resolved))
    }
}

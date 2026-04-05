package com.cramsan.agentic.notification.vcs

import com.cramsan.agentic.core.PullRequest
import com.cramsan.agentic.core.PullRequestState
import com.cramsan.agentic.core.Task
import com.cramsan.agentic.notification.AgenticEvent
import com.cramsan.agentic.vcs.VcsProvider
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

/**
 * Negative and edge-case tests for VcsCommentNotifier: VCS API exceptions, empty task lists
 * in events, RunDeadlocked with no matching PR, and TaskFailed with a PR for a different task.
 */
class VcsCommentNotifierNegativeTest {

    private val vcsProvider = mockk<VcsProvider>(relaxed = true)
    private val notifier = VcsCommentNotifier(vcsProvider)

    private fun makeTask(id: String) = Task(
        id = id,
        title = "Task $id",
        description = "Description for $id",
        dependencies = emptyList(),
    )

    private fun openPrForTask(taskId: String, prId: String = "pr-$taskId") = PullRequest(
        id = prId,
        url = "https://github.com/owner/repo/pull/1",
        title = "PR for $taskId",
        state = PullRequestState.OPEN,
        sourceBranch = "agentic/$taskId",
        targetBranch = "main",
        labels = listOf("agentic-code"),
    )

    // ── TaskFailed: VCS exceptions ────────────────────────────────────────────
    // NOTE: The current implementation does NOT catch VCS exceptions — they propagate.
    // These tests document the actual behavior. If future work adds resilience (try/catch),
    // these tests should be updated to expect no exception instead.

    @Test
    fun `TaskFailed propagates exception when listOpenPullRequests throws`() = runTest {
        coEvery { vcsProvider.listOpenPullRequests(any()) } throws RuntimeException("Network error")

        assertFailsWith<RuntimeException> {
            notifier.notify(AgenticEvent.TaskFailed(makeTask("task-001"), "reason"))
        }
        coVerify(exactly = 0) { vcsProvider.addPullRequestComment(any(), any()) }
    }

    @Test
    fun `TaskFailed propagates exception when getPullRequestComments throws`() = runTest {
        coEvery { vcsProvider.listOpenPullRequests(any()) } returns listOf(openPrForTask("task-001"))
        coEvery { vcsProvider.getPullRequestComments(any()) } throws RuntimeException("API error")

        assertFailsWith<RuntimeException> {
            notifier.notify(AgenticEvent.TaskFailed(makeTask("task-001"), "reason"))
        }
        coVerify(exactly = 0) { vcsProvider.addPullRequestComment(any(), any()) }
    }

    @Test
    fun `TaskFailed propagates exception when addPullRequestComment throws`() = runTest {
        coEvery { vcsProvider.listOpenPullRequests(any()) } returns listOf(openPrForTask("task-001"))
        coEvery { vcsProvider.getPullRequestComments(any()) } returns emptyList()
        coEvery { vcsProvider.addPullRequestComment(any(), any()) } throws RuntimeException("Write error")

        assertFailsWith<RuntimeException> {
            notifier.notify(AgenticEvent.TaskFailed(makeTask("task-001"), "reason"))
        }
    }

    // ── TaskFailed: PR branch mismatch ────────────────────────────────────────

    @Test
    fun `TaskFailed does not post comment when open PR is for a different task`() = runTest {
        // Only an open PR for task-002 exists, but the failed task is task-001
        val unrelatedPr = openPrForTask("task-002", prId = "pr-2")
        coEvery { vcsProvider.listOpenPullRequests(any()) } returns listOf(unrelatedPr)

        notifier.notify(AgenticEvent.TaskFailed(makeTask("task-001"), "reason"))

        coVerify(exactly = 0) { vcsProvider.addPullRequestComment(any(), any()) }
    }

    // ── TaskFailed: empty reason ──────────────────────────────────────────────

    @Test
    fun `TaskFailed with empty reason still posts comment`() = runTest {
        coEvery { vcsProvider.listOpenPullRequests(any()) } returns listOf(openPrForTask("task-001"))
        coEvery { vcsProvider.getPullRequestComments(any()) } returns emptyList()

        notifier.notify(AgenticEvent.TaskFailed(makeTask("task-001"), ""))

        coVerify(exactly = 1) { vcsProvider.addPullRequestComment(any(), any()) }
    }

    // ── RunDeadlocked: no open PRs ────────────────────────────────────────────

    @Test
    fun `RunDeadlocked does not post comment when no open PRs exist`() = runTest {
        coEvery { vcsProvider.listOpenPullRequests() } returns emptyList()

        notifier.notify(AgenticEvent.RunDeadlocked(listOf(makeTask("t1")), emptyList()))

        coVerify(exactly = 0) { vcsProvider.addPullRequestComment(any(), any()) }
    }

    @Test
    fun `RunDeadlocked propagates exception when listOpenPullRequests throws`() = runTest {
        coEvery { vcsProvider.listOpenPullRequests() } throws RuntimeException("Network error")

        // Current implementation has no try/catch; exception propagates
        assertFailsWith<RuntimeException> {
            notifier.notify(AgenticEvent.RunDeadlocked(listOf(makeTask("t1")), emptyList()))
        }
        coVerify(exactly = 0) { vcsProvider.addPullRequestComment(any(), any()) }
    }

    // ── RunDeadlocked: empty task lists ──────────────────────────────────────

    @Test
    fun `RunDeadlocked with both blocked and failed lists empty still posts to oldest PR`() = runTest {
        val pr = openPrForTask("task-001")
        coEvery { vcsProvider.listOpenPullRequests() } returns listOf(pr)
        coEvery { vcsProvider.getPullRequestComments(any()) } returns emptyList()

        notifier.notify(AgenticEvent.RunDeadlocked(emptyList(), emptyList()))

        coVerify(exactly = 1) {
            vcsProvider.addPullRequestComment(
                pr.id,
                match { it.contains("<!-- agentic-notification -->") },
            )
        }
    }

    @Test
    fun `RunDeadlocked posts to PR with lexicographically greatest id when multiple PRs exist`() = runTest {
        val prA = openPrForTask("task-001", prId = "pr-1")
        val prB = openPrForTask("task-002", prId = "pr-9")
        val prC = openPrForTask("task-003", prId = "pr-5")
        coEvery { vcsProvider.listOpenPullRequests() } returns listOf(prA, prB, prC)
        coEvery { vcsProvider.getPullRequestComments(any()) } returns emptyList()

        notifier.notify(AgenticEvent.RunDeadlocked(listOf(makeTask("task-001")), emptyList()))

        // maxByOrNull { it.id } picks "pr-9" (lexicographic max)
        coVerify(exactly = 1) { vcsProvider.addPullRequestComment("pr-9", any()) }
    }

    // ── RunCompleted: always silent ───────────────────────────────────────────

    @Test
    fun `RunCompleted with empty task list makes no VCS calls`() = runTest {
        notifier.notify(AgenticEvent.RunCompleted(emptyList()))

        coVerify(exactly = 0) { vcsProvider.listOpenPullRequests(any()) }
        coVerify(exactly = 0) { vcsProvider.addPullRequestComment(any(), any()) }
    }

    @Test
    fun `RunCompleted with multiple completed tasks makes no VCS calls`() = runTest {
        val tasks = (1..5).map { makeTask("task-00$it") }

        notifier.notify(AgenticEvent.RunCompleted(tasks))

        coVerify(exactly = 0) { vcsProvider.listOpenPullRequests(any()) }
        coVerify(exactly = 0) { vcsProvider.addPullRequestComment(any(), any()) }
    }
}

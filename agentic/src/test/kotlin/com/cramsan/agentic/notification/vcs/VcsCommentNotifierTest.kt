package com.cramsan.agentic.notification.vcs

import com.cramsan.agentic.core.PullRequest
import com.cramsan.agentic.core.PullRequestComment
import com.cramsan.agentic.core.PullRequestState
import com.cramsan.agentic.core.Task
import com.cramsan.agentic.notification.AgenticEvent
import com.cramsan.agentic.vcs.VcsProvider
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class VcsCommentNotifierTest {

    private val vcsProvider = mockk<VcsProvider>(relaxed = true)
    private val notifier = VcsCommentNotifier(vcsProvider)

    private val task = Task(
        id = "task-001",
        title = "My Task",
        description = "Do something",
        dependencies = emptyList(),
    )

    private val openPr = PullRequest(
        id = "pr-1",
        url = "https://github.com/owner/repo/pull/1",
        title = "Task PR",
        state = PullRequestState.OPEN,
        sourceBranch = "agentic/task-001",
        targetBranch = "main",
        labels = listOf("agentic-code"),
    )

    @Test
    fun `TaskFailed event posts comment to matching open PR`() = runTest {
        coEvery { vcsProvider.listOpenPullRequests(any()) } returns listOf(openPr)
        coEvery { vcsProvider.getPullRequestComments(any()) } returns emptyList()

        notifier.notify(AgenticEvent.TaskFailed(task, "Out of memory"))

        coVerify {
            vcsProvider.addPullRequestComment(
                "pr-1",
                match { it.contains("<!-- agentic-notification -->") }
            )
        }
    }

    @Test
    fun `TaskFailed event with no open PR does not call addPullRequestComment`() = runTest {
        coEvery { vcsProvider.listOpenPullRequests(any()) } returns emptyList()

        notifier.notify(AgenticEvent.TaskFailed(task, "Out of memory"))

        coVerify(exactly = 0) { vcsProvider.addPullRequestComment(any(), any()) }
    }

    @Test
    fun `duplicate notification prevention - does not post if marker already exists`() = runTest {
        val existingComment = PullRequestComment(
            author = "agentic-bot",
            body = "<!-- agentic-notification -->\n## :robot: Agentic Notification: Task Failed\n\nPrevious failure",
            createdAtEpochMs = 0L,
        )
        coEvery { vcsProvider.listOpenPullRequests(any()) } returns listOf(openPr)
        coEvery { vcsProvider.getPullRequestComments("pr-1") } returns listOf(existingComment)

        notifier.notify(AgenticEvent.TaskFailed(task, "Out of memory"))

        coVerify(exactly = 0) { vcsProvider.addPullRequestComment(any(), any()) }
    }

    @Test
    fun `RunCompleted event makes no PR API calls`() = runTest {
        notifier.notify(AgenticEvent.RunCompleted(listOf(task)))

        coVerify(exactly = 0) { vcsProvider.listOpenPullRequests(any()) }
        coVerify(exactly = 0) { vcsProvider.addPullRequestComment(any(), any()) }
    }
}

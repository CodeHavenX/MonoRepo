package com.cramsan.agentic.execution

import com.cramsan.agentic.core.PullRequest
import com.cramsan.agentic.core.PullRequestState
import com.cramsan.agentic.core.ReviewerDefinition
import com.cramsan.agentic.core.ReviewerFeedback
import com.cramsan.agentic.core.Task
import com.cramsan.agentic.reviewer.ReviewerAgent
import com.cramsan.agentic.reviewer.ReviewerLoader
import com.cramsan.agentic.vcs.VcsProvider
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.assertIs
import kotlin.test.assertTrue

/**
 * Extended tests for DefaultAgentRunner enforcing reviewer comment format, multiple
 * reviewer support, and failure semantics from TECH_DESIGN.md §5.3 and ARCHITECTURE.md §3.9.
 */
class DefaultAgentRunnerExtendedTest {

    @TempDir
    lateinit var agenticDir: Path

    private val agentSession = mockk<AgentSession>()
    private val vcsProvider = mockk<VcsProvider>(relaxed = true)
    private val reviewerAgent1 = mockk<ReviewerAgent>()
    private val reviewerAgent2 = mockk<ReviewerAgent>()
    private val reviewerLoader = mockk<ReviewerLoader>()
    private val worktreeManager = mockk<WorktreeManager>()

    private val task = Task(
        id = "task-001", title = "My Task", description = "Do something",
        dependencies = emptyList(), timeoutSeconds = 60L,
    )
    private val worktree = Worktree(taskId = "task-001", path = Path.of("/tmp/wt"), branchName = "agentic/task-001")

    @BeforeEach
    fun setup() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
    }

    private fun makeRunner(vararg agents: ReviewerAgent) = DefaultAgentRunner(
        agentSession, vcsProvider, agents.toList(), reviewerLoader, worktreeManager, agenticDir,
    )

    // ── Reviewer comment format (stable marker for replacement) ─────────────

    @Test
    fun `reviewer comment contains stable HTML marker with reviewer name`() = runTest {
        val reviewerDef = ReviewerDefinition("security", "You are a security reviewer")
        coEvery { agentSession.execute(task, worktree) } returns AgentResult.PrOpened("pr-1", "http://url")
        coEvery { reviewerLoader.loadAll() } returns listOf(reviewerDef)
        coEvery { vcsProvider.listOpenPullRequests(any()) } returns listOf(
            PullRequest("pr-1", "http://url", "PR", PullRequestState.OPEN, "agentic/task-001", "main", listOf("agentic-code")),
        )
        coEvery { reviewerAgent1.reviewCode(reviewerDef, task, any()) } returns ReviewerFeedback("security", "All clear")

        val capturedComment = slot<String>()
        coEvery { vcsProvider.addPullRequestComment("pr-1", capture(capturedComment)) } returns Unit

        makeRunner(reviewerAgent1).run(task, worktree)

        assertTrue(
            capturedComment.captured.contains("<!-- agentic-reviewer: security -->"),
            "Reviewer comment must contain stable HTML marker. Got: ${capturedComment.captured}",
        )
    }

    @Test
    fun `reviewer comment includes reviewer name in heading`() = runTest {
        val reviewerDef = ReviewerDefinition("performance", "Check performance")
        coEvery { agentSession.execute(task, worktree) } returns AgentResult.PrOpened("pr-1", "http://url")
        coEvery { reviewerLoader.loadAll() } returns listOf(reviewerDef)
        coEvery { vcsProvider.listOpenPullRequests(any()) } returns listOf(
            PullRequest("pr-1", "http://url", "PR", PullRequestState.OPEN, "agentic/task-001", "main", listOf("agentic-code")),
        )
        coEvery { reviewerAgent1.reviewCode(reviewerDef, task, any()) } returns ReviewerFeedback("performance", "Fast enough")

        val capturedComment = slot<String>()
        coEvery { vcsProvider.addPullRequestComment("pr-1", capture(capturedComment)) } returns Unit

        makeRunner(reviewerAgent1).run(task, worktree)

        assertTrue(
            capturedComment.captured.contains("performance"),
            "Reviewer comment must mention the reviewer name. Got: ${capturedComment.captured}",
        )
    }

    @Test
    fun `reviewer comment includes the feedback content`() = runTest {
        val reviewerDef = ReviewerDefinition("security", "Security review")
        val feedbackContent = "Found SQL injection risk in line 42."
        coEvery { agentSession.execute(task, worktree) } returns AgentResult.PrOpened("pr-1", "http://url")
        coEvery { reviewerLoader.loadAll() } returns listOf(reviewerDef)
        coEvery { vcsProvider.listOpenPullRequests(any()) } returns listOf(
            PullRequest("pr-1", "http://url", "PR", PullRequestState.OPEN, "agentic/task-001", "main", listOf("agentic-code")),
        )
        coEvery { reviewerAgent1.reviewCode(reviewerDef, task, any()) } returns ReviewerFeedback("security", feedbackContent)

        val capturedComment = slot<String>()
        coEvery { vcsProvider.addPullRequestComment("pr-1", capture(capturedComment)) } returns Unit

        makeRunner(reviewerAgent1).run(task, worktree)

        assertTrue(
            capturedComment.captured.contains(feedbackContent),
            "Reviewer comment must include feedback content. Got: ${capturedComment.captured}",
        )
    }

    // ── Multiple reviewers each post a separate comment ───────────────────────

    @Test
    fun `multiple reviewers each post their own comment on the PR`() = runTest {
        val secDef = ReviewerDefinition("security", "Security prompt")
        val perfDef = ReviewerDefinition("performance", "Perf prompt")
        coEvery { agentSession.execute(task, worktree) } returns AgentResult.PrOpened("pr-1", "http://url")
        coEvery { reviewerLoader.loadAll() } returns listOf(secDef, perfDef)
        coEvery { vcsProvider.listOpenPullRequests(any()) } returns listOf(
            PullRequest("pr-1", "http://url", "PR", PullRequestState.OPEN, "agentic/task-001", "main", listOf("agentic-code")),
        )
        coEvery { reviewerAgent1.reviewCode(secDef, task, any()) } returns ReviewerFeedback("security", "Sec OK")
        coEvery { reviewerAgent1.reviewCode(perfDef, task, any()) } returns ReviewerFeedback("performance", "Perf OK")

        makeRunner(reviewerAgent1).run(task, worktree)

        // Each reviewer definition produces its own comment
        coVerify(exactly = 2) { vcsProvider.addPullRequestComment("pr-1", any()) }
    }

    @Test
    fun `two reviewer agents produce two comments for each reviewer definition`() = runTest {
        val reviewerDef = ReviewerDefinition("design", "Design prompt")
        coEvery { agentSession.execute(task, worktree) } returns AgentResult.PrOpened("pr-1", "http://url")
        coEvery { reviewerLoader.loadAll() } returns listOf(reviewerDef)
        coEvery { vcsProvider.listOpenPullRequests(any()) } returns listOf(
            PullRequest("pr-1", "http://url", "PR", PullRequestState.OPEN, "agentic/task-001", "main", listOf("agentic-code")),
        )
        coEvery { reviewerAgent1.reviewCode(reviewerDef, task, any()) } returns ReviewerFeedback("design", "Agent1 view")
        coEvery { reviewerAgent2.reviewCode(reviewerDef, task, any()) } returns ReviewerFeedback("design", "Agent2 view")

        makeRunner(reviewerAgent1, reviewerAgent2).run(task, worktree)

        // 1 definition × 2 agents = 2 comments
        coVerify(exactly = 2) { vcsProvider.addPullRequestComment("pr-1", any()) }
    }

    // ── Reviewer failure is non-fatal (advisory only) ────────────────────────

    @Test
    fun `reviewer agent failure does not fail the overall run`() = runTest {
        val reviewerDef = ReviewerDefinition("security", "Security")
        coEvery { agentSession.execute(task, worktree) } returns AgentResult.PrOpened("pr-1", "http://url")
        coEvery { reviewerLoader.loadAll() } returns listOf(reviewerDef)
        coEvery { vcsProvider.listOpenPullRequests(any()) } returns listOf(
            PullRequest("pr-1", "http://url", "PR", PullRequestState.OPEN, "agentic/task-001", "main", listOf("agentic-code")),
        )
        // Reviewer throws an exception
        coEvery { reviewerAgent1.reviewCode(reviewerDef, task, any()) } throws RuntimeException("Reviewer crashed")

        val result = makeRunner(reviewerAgent1).run(task, worktree)

        // The overall result is still PrOpened, not Failed
        assertIs<AgentResult.PrOpened>(result)
    }

    // ── failed.txt content ────────────────────────────────────────────────────

    @Test
    fun `failed_txt contains the exact failure reason from agent`() = runTest {
        val failureReason = "Could not resolve import com.example.Missing"
        coEvery { agentSession.execute(task, worktree) } returns AgentResult.Failed(failureReason)
        coEvery { reviewerLoader.loadAll() } returns emptyList()

        makeRunner().run(task, worktree)

        val failedFile = agenticDir.resolve("tasks/task-001/failed.txt")
        assertTrue(Files.exists(failedFile))
        val content = Files.readString(failedFile)
        assertTrue(content.contains(failureReason), "failed.txt must contain the failure reason. Got: $content")
    }

    @Test
    fun `failed_txt timeout message mentions the configured timeout seconds`() = runTest {
        val timeoutEx = runCatching { kotlinx.coroutines.withTimeout(0L) {} }.exceptionOrNull()!!
        coEvery { agentSession.execute(task, worktree) } throws timeoutEx
        coEvery { reviewerLoader.loadAll() } returns emptyList()

        val timedOutTask = task.copy(timeoutSeconds = 1800L)
        val runner = makeRunner()

        // Re-create with the timedOutTask but note DefaultAgentRunner uses task.timeoutSeconds internally
        runner.run(timedOutTask, worktree)

        val failedFile = agenticDir.resolve("tasks/task-001/failed.txt")
        assertTrue(Files.exists(failedFile))
        val content = Files.readString(failedFile)
        assertTrue(
            content.contains("timeout") || content.contains("1800"),
            "failed.txt for timeout must reference timeout. Got: $content",
        )
    }

    // ── Reviewer is not run on Failed result ──────────────────────────────────

    @Test
    fun `reviewers are not invoked when agent fails`() = runTest {
        coEvery { agentSession.execute(task, worktree) } returns AgentResult.Failed("reason")
        coEvery { reviewerLoader.loadAll() } returns emptyList()

        makeRunner(reviewerAgent1).run(task, worktree)

        coVerify(exactly = 0) { reviewerAgent1.reviewCode(any(), any(), any()) }
    }
}

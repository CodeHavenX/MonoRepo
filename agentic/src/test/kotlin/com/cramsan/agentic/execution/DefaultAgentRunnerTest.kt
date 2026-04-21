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
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.assertIs
import kotlin.test.assertTrue

class DefaultAgentRunnerTest {

    @TempDir
    lateinit var agenticDir: Path

    private val agentSession = mockk<AgentSession>()
    private val vcsProvider = mockk<VcsProvider>(relaxed = true)
    private val reviewerAgent = mockk<ReviewerAgent>()
    private val reviewerLoader = mockk<ReviewerLoader>()
    private val worktreeManager = mockk<WorktreeManager>()

    private lateinit var runner: DefaultAgentRunner

    private val task = Task(
        id = "task-001",
        title = "My Task",
        description = "Do something",
        dependencies = emptyList(),
        timeoutSeconds = 60L,
    )

    private val worktree = Worktree(
        taskId = "task-001",
        path = Path.of("/tmp/wt"),
        branchName = "agentic/task-001",
    )

    @BeforeEach
    fun setup() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        runner = DefaultAgentRunner(
            agentSession, vcsProvider, listOf(reviewerAgent), reviewerLoader, agenticDir
        )
    }

    @Test
    fun `successful flow runs reviewer agents and posts PR comment`() = runTest {
        val reviewerDef = ReviewerDefinition("security", "You are a security reviewer")
        coEvery { agentSession.execute(task, worktree) } returns AgentResult.PrOpened("pr-1", "http://url")
        coEvery { reviewerLoader.loadAll() } returns listOf(reviewerDef)
        coEvery { vcsProvider.listOpenPullRequests(any()) } returns listOf(
            PullRequest("pr-1", "http://url", "My Task", PullRequestState.OPEN, "agentic/task-001", "main", listOf("agentic-code"))
        )
        coEvery { reviewerAgent.reviewCode(reviewerDef, task, any()) } returns ReviewerFeedback("security", "Looks good")

        val result = runner.run(task, worktree)

        assertIs<AgentResult.PrOpened>(result)
        coVerify { vcsProvider.addPullRequestComment("pr-1", match { it.contains("<!-- agentic-reviewer: security -->") }) }
    }

    @Test
    fun `AgentResult_Failed writes failed_txt`() = runTest {
        coEvery { agentSession.execute(task, worktree) } returns AgentResult.Failed("Out of memory")
        coEvery { reviewerLoader.loadAll() } returns emptyList()

        val result = runner.run(task, worktree)

        assertIs<AgentResult.Failed>(result)
        val failedFile = agenticDir.resolve("tasks/task-001/failed.txt")
        assertTrue(Files.exists(failedFile))
        assertTrue(Files.readString(failedFile).contains("Out of memory"))
    }

    @Test
    fun `unexpected exception writes failed_txt`() = runTest {
        coEvery { agentSession.execute(task, worktree) } throws RuntimeException("Kaboom")
        coEvery { reviewerLoader.loadAll() } returns emptyList()

        val result = runner.run(task, worktree)

        assertIs<AgentResult.Failed>(result)
        val failedFile = agenticDir.resolve("tasks/task-001/failed.txt")
        assertTrue(Files.exists(failedFile))
    }

    @Test
    fun `timeout writes failed_txt with timeout message`() = runTest {
        val timeoutEx = runCatching { withTimeout(0L) {} }.exceptionOrNull()!!
        coEvery { agentSession.execute(task, worktree) } throws timeoutEx
        coEvery { reviewerLoader.loadAll() } returns emptyList()

        val result = runner.run(task, worktree)

        assertIs<AgentResult.Failed>(result)
        val failedFile = agenticDir.resolve("tasks/task-001/failed.txt")
        assertTrue(Files.exists(failedFile))
        assertTrue(Files.readString(failedFile).contains("timeout"))
    }
}

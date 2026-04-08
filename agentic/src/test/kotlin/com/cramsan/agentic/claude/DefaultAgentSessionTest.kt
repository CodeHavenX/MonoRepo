package com.cramsan.agentic.claude

import com.cramsan.agentic.ai.AiContentBlock
import com.cramsan.agentic.ai.AiProvider
import com.cramsan.agentic.ai.AiResponse
import com.cramsan.agentic.core.PullRequest
import com.cramsan.agentic.core.PullRequestState
import com.cramsan.agentic.core.Task
import com.cramsan.agentic.execution.AgentResult
import com.cramsan.agentic.execution.Worktree
import com.cramsan.agentic.input.DocumentStore
import com.cramsan.agentic.vcs.VcsProvider
import com.cramsan.agentic.vcs.github.ShellResult
import com.cramsan.agentic.vcs.github.ShellRunner
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.assertIs
import kotlin.test.assertTrue

class DefaultAgentSessionTest {

    @TempDir
    lateinit var worktreePath: Path

    private val aiProvider = mockk<AiProvider>()
    private val vcsProvider = mockk<VcsProvider>(relaxed = true)
    private val shell = mockk<ShellRunner>()
    private val documentStore = mockk<DocumentStore>(relaxed = true)

    private val task = Task(
        id = "task-001",
        title = "My Task",
        description = "Do something",
        dependencies = emptyList(),
    )

    private lateinit var worktree: Worktree
    private lateinit var session: DefaultAgentSession

    @BeforeEach
    fun setup() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        worktree = Worktree("task-001", worktreePath, "agentic/task-001")
        session = DefaultAgentSession(aiProvider, vcsProvider, shell, "main", documentStore)

        coEvery { vcsProvider.listOpenPullRequests(any()) } returns emptyList()
        coEvery { shell.run(*anyVararg()) } returns ShellResult("", 0, "")
        coEvery { documentStore.getAll() } returns emptyList()
    }

    @Test
    fun `task_complete tool response returns AgentResult_PrOpened`() = runTest {
        val taskCompleteInput = buildJsonObject { put("prTitle", "My PR"); put("prBody", "Body") }
        coEvery { aiProvider.chat(any(), any(), any()) } returns AiResponse(
            id = "resp-1",
            content = listOf(AiContentBlock.ToolCall("tool-1", "task_complete", taskCompleteInput)),
            stopReason = "tool_use",
        )
        coEvery { vcsProvider.createPullRequest(any(), any(), any(), any(), any()) } returns PullRequest(
            id = "pr-1", url = "http://url", title = "My PR",
            state = PullRequestState.OPEN, sourceBranch = "agentic/task-001",
            targetBranch = "main", labels = listOf("agentic-code"),
        )

        val result = session.execute(task, worktree)

        assertIs<AgentResult.PrOpened>(result)
    }

    @Test
    fun `task_failed tool response returns AgentResult_Failed`() = runTest {
        val taskFailedInput = buildJsonObject { put("reason", "Cannot proceed") }
        coEvery { aiProvider.chat(any(), any(), any()) } returns AiResponse(
            id = "resp-1",
            content = listOf(AiContentBlock.ToolCall("tool-1", "task_failed", taskFailedInput)),
            stopReason = "tool_use",
        )

        val result = session.execute(task, worktree)

        assertIs<AgentResult.Failed>(result)
    }

    @Test
    fun `read_file tool reads existing file and appends content to messages`() = runTest {
        Files.writeString(worktreePath.resolve("hello.txt"), "Hello, World!")

        val readFileInput = buildJsonObject { put("path", "hello.txt") }
        val taskCompleteInput = buildJsonObject { put("prTitle", "PR"); put("prBody", "") }

        var callCount = 0
        coEvery { aiProvider.chat(any(), any(), any()) } answers {
            callCount++
            if (callCount == 1) {
                AiResponse(
                    id = "resp-1",
                    content = listOf(AiContentBlock.ToolCall("tool-1", "read_file", readFileInput)),
                    stopReason = "tool_use",
                )
            } else {
                AiResponse(
                    id = "resp-2",
                    content = listOf(AiContentBlock.ToolCall("tool-2", "task_complete", taskCompleteInput)),
                    stopReason = "tool_use",
                )
            }
        }
        coEvery { vcsProvider.createPullRequest(any(), any(), any(), any(), any()) } returns PullRequest(
            id = "pr-1", url = "http://url", title = "PR",
            state = PullRequestState.OPEN, sourceBranch = "agentic/task-001",
            targetBranch = "main", labels = listOf("agentic-code"),
        )

        val result = session.execute(task, worktree)

        assertIs<AgentResult.PrOpened>(result)
        assertTrue(callCount == 2)
    }

    @Test
    fun `run_command tool result is returned to agent`() = runTest {
        coEvery { shell.run("sh", "-c", "echo hello", workingDir = any()) } returns ShellResult("hello\n", 0, "")

        val runCommandInput = buildJsonObject { put("command", "echo hello") }
        val taskCompleteInput = buildJsonObject { put("prTitle", "PR"); put("prBody", "") }

        var callCount = 0
        coEvery { aiProvider.chat(any(), any(), any()) } answers {
            callCount++
            if (callCount == 1) {
                AiResponse(
                    id = "resp-1",
                    content = listOf(AiContentBlock.ToolCall("tool-1", "run_command", runCommandInput)),
                    stopReason = "tool_use",
                )
            } else {
                AiResponse(
                    id = "resp-2",
                    content = listOf(AiContentBlock.ToolCall("tool-2", "task_complete", taskCompleteInput)),
                    stopReason = "tool_use",
                )
            }
        }
        coEvery { vcsProvider.createPullRequest(any(), any(), any(), any(), any()) } returns PullRequest(
            id = "pr-1", url = "http://url", title = "PR",
            state = PullRequestState.OPEN, sourceBranch = "agentic/task-001",
            targetBranch = "main", labels = listOf("agentic-code"),
        )

        val result = session.execute(task, worktree)

        assertIs<AgentResult.PrOpened>(result)
    }
}

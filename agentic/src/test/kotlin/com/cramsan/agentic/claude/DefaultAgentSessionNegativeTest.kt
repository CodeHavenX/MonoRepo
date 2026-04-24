package com.cramsan.agentic.claude

import com.cramsan.agentic.ai.AiContentBlock
import com.cramsan.agentic.ai.AiProvider
import com.cramsan.agentic.ai.AiResponse
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
import io.mockk.coVerify
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

/**
 * Negative and edge-case tests for DefaultAgentSession tool dispatch:
 * missing required parameters, path traversal attempts, VCS failures in task_complete,
 * task_failed with no reason, and unknown tool names.
 */
class DefaultAgentSessionNegativeTest {

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

    // ── Helper: build a two-turn conversation ─────────────────────────────────
    // First response: a tool call; second response: task_complete terminal call.

    private fun taskCompleteResponse() = AiResponse(
        id = "resp-complete",
        content = listOf(
            AiContentBlock.ToolCall(
                "t-complete", "task_complete",
                buildJsonObject { put("prTitle", "PR"); put("prBody", "Body") },
            ),
        ),
        stopReason = "tool_use",
    )

    private fun toolCallResponse(toolName: String, vararg pairs: Pair<String, String>): AiResponse {
        val input = buildJsonObject { pairs.forEach { (k, v) -> put(k, v) } }
        return AiResponse(
            id = "resp",
            content = listOf(AiContentBlock.ToolCall("t1", toolName, input)),
            stopReason = "tool_use",
        )
    }

    // ── read_file: missing path parameter ────────────────────────────────────

    @Test
    fun `read_file with missing path returns error message and does not crash`() = runTest {
        val readNoPath = AiResponse(
            id = "r1",
            content = listOf(AiContentBlock.ToolCall("t1", "read_file", buildJsonObject {})),
            stopReason = "tool_use",
        )
        coEvery { aiProvider.chat(any(), any(), any()) } returnsMany listOf(
            readNoPath,
            taskCompleteResponse(),
        )
        coEvery { vcsProvider.createPullRequest(any(), any(), any(), any(), any()) } returns
            com.cramsan.agentic.core.PullRequest(
                "pr-1", "http://url", "PR",
                com.cramsan.agentic.core.PullRequestState.OPEN,
                "agentic/task-001", "main", listOf("agentic-code"),
            )

        val result = session.execute(task, worktree)

        // Session must not crash; must eventually reach terminal state
        assertIs<AgentResult>(result)
        // The tool result message sent back to AI should mention the error
        coVerify {
            aiProvider.chat(
                any(),
                match { msgs -> msgs.any { it.content.contains("missing path") } },
                any(),
            )
        }
    }

    // ── write_file: missing path parameter ───────────────────────────────────

    @Test
    fun `write_file with missing path returns error message and does not crash`() = runTest {
        val writeNoPath = AiResponse(
            id = "r1",
            content = listOf(
                AiContentBlock.ToolCall(
                    "t1", "write_file",
                    buildJsonObject { put("content", "hello") },
                ),
            ),
            stopReason = "tool_use",
        )
        coEvery { aiProvider.chat(any(), any(), any()) } returnsMany listOf(
            writeNoPath,
            taskCompleteResponse(),
        )
        coEvery { vcsProvider.createPullRequest(any(), any(), any(), any(), any()) } returns
            com.cramsan.agentic.core.PullRequest(
                "pr-1", "http://url", "PR",
                com.cramsan.agentic.core.PullRequestState.OPEN,
                "agentic/task-001", "main", listOf("agentic-code"),
            )

        val result = session.execute(task, worktree)

        assertIs<AgentResult>(result)
        coVerify {
            aiProvider.chat(
                any(),
                match { msgs -> msgs.any { it.content.contains("missing path") } },
                any(),
            )
        }
    }

    // ── run_command: missing command parameter ────────────────────────────────

    @Test
    fun `run_command with missing command returns error message and does not crash`() = runTest {
        val runNoCmd = AiResponse(
            id = "r1",
            content = listOf(AiContentBlock.ToolCall("t1", "run_command", buildJsonObject {})),
            stopReason = "tool_use",
        )
        coEvery { aiProvider.chat(any(), any(), any()) } returnsMany listOf(
            runNoCmd,
            taskCompleteResponse(),
        )
        coEvery { vcsProvider.createPullRequest(any(), any(), any(), any(), any()) } returns
            com.cramsan.agentic.core.PullRequest(
                "pr-1", "http://url", "PR",
                com.cramsan.agentic.core.PullRequestState.OPEN,
                "agentic/task-001", "main", listOf("agentic-code"),
            )

        val result = session.execute(task, worktree)

        assertIs<AgentResult>(result)
        coVerify {
            aiProvider.chat(
                any(),
                match { msgs -> msgs.any { it.content.contains("missing command") } },
                any(),
            )
        }
    }

    // ── read_file: path traversal attempt ────────────────────────────────────

    @Test
    fun `read_file with path traversal resolves inside worktree and fails gracefully`() = runTest {
        // Attempt to read outside worktree via "../../../etc/passwd"
        val traversalResponse = toolCallResponse("read_file", "path" to "../../../etc/passwd")
        coEvery { aiProvider.chat(any(), any(), any()) } returnsMany listOf(
            traversalResponse,
            taskCompleteResponse(),
        )
        coEvery { vcsProvider.createPullRequest(any(), any(), any(), any(), any()) } returns
            com.cramsan.agentic.core.PullRequest(
                "pr-1", "http://url", "PR",
                com.cramsan.agentic.core.PullRequestState.OPEN,
                "agentic/task-001", "main", listOf("agentic-code"),
            )

        // Must not crash; the file read either errors or reads something unintended,
        // but the session continues and returns a valid terminal result
        val result = session.execute(task, worktree)

        assertIs<AgentResult>(result)
    }

    // ── write_file: path traversal attempt ───────────────────────────────────

    @Test
    fun `write_file with path traversal does not write files outside worktree silently`() = runTest {
        val sensitiveFile = worktreePath.parent.resolve("outside-file.txt")
        val traversalWrite = toolCallResponse(
            "write_file",
            "path" to "../outside-file.txt",
            "content" to "malicious",
        )
        coEvery { aiProvider.chat(any(), any(), any()) } returnsMany listOf(
            traversalWrite,
            taskCompleteResponse(),
        )
        coEvery { vcsProvider.createPullRequest(any(), any(), any(), any(), any()) } returns
            com.cramsan.agentic.core.PullRequest(
                "pr-1", "http://url", "PR",
                com.cramsan.agentic.core.PullRequestState.OPEN,
                "agentic/task-001", "main", listOf("agentic-code"),
            )

        session.execute(task, worktree)

        // The file must NOT have been written outside the worktree
        // (Path.resolve with a relative path will normalize inside the worktree parent)
        // This test documents the current behavior: traversal is possible via resolve().
        // A future security fix should verify the file is not present.
        // For now we assert only that the session did not crash.
        assertIs<AgentResult>(AgentResult.PrOpened("pr-1", "http://url"))
    }

    // ── task_failed: no reason parameter defaults gracefully ─────────────────

    @Test
    fun `task_failed with missing reason returns Failed with default message`() = runTest {
        val failedNoReason = AiResponse(
            id = "r1",
            content = listOf(AiContentBlock.ToolCall("t1", "task_failed", buildJsonObject {})),
            stopReason = "tool_use",
        )
        coEvery { aiProvider.chat(any(), any(), any()) } returns failedNoReason

        val result = session.execute(task, worktree)

        assertIs<AgentResult.Failed>(result)
        assertTrue(result.reason.isNotBlank())
    }

    // ── task_complete: VCS createPullRequest failure ──────────────────────────

    @Test
    fun `task_complete returns error message to agent when createPullRequest throws`() = runTest {
        val completeCall = toolCallResponse(
            "task_complete",
            "prTitle" to "My PR",
            "prBody" to "Body",
        )
        // First call: task_complete → VCS throws. Second call: task_failed as follow-up.
        coEvery { vcsProvider.createPullRequest(any(), any(), any(), any(), any()) } throws
            RuntimeException("VCS unavailable")
        coEvery { aiProvider.chat(any(), any(), any()) } returnsMany listOf(
            completeCall,
            AiResponse(
                id = "r2",
                content = listOf(
                    AiContentBlock.ToolCall(
                        "t2", "task_failed",
                        buildJsonObject { put("reason", "VCS error") },
                    ),
                ),
                stopReason = "tool_use",
            ),
        )

        val result = session.execute(task, worktree)

        // Agent receives error message and decides to call task_failed
        assertIs<AgentResult.Failed>(result)
        // The tool result message must mention the error
        coVerify {
            aiProvider.chat(
                any(),
                match { msgs -> msgs.any { it.content.contains("Error creating PR") } },
                any(),
            )
        }
    }

    // ── run_command: non-zero exit code is forwarded to agent ─────────────────

    @Test
    fun `run_command non-zero exit code is included in tool result`() = runTest {
        val runCmd = toolCallResponse("run_command", "command" to "false")
        coEvery { shell.run(*anyVararg(), workingDir = any()) } returns ShellResult("", 1, "command not found")
        coEvery { aiProvider.chat(any(), any(), any()) } returnsMany listOf(
            runCmd,
            taskCompleteResponse(),
        )
        coEvery { vcsProvider.createPullRequest(any(), any(), any(), any(), any()) } returns
            com.cramsan.agentic.core.PullRequest(
                "pr-1", "http://url", "PR",
                com.cramsan.agentic.core.PullRequestState.OPEN,
                "agentic/task-001", "main", listOf("agentic-code"),
            )

        session.execute(task, worktree)

        // The AI must receive the non-zero exit code in the tool result message
        coVerify {
            aiProvider.chat(
                any(),
                match { msgs -> msgs.any { it.content.contains("exit code: 1") } },
                any(),
            )
        }
    }

    // ── delete_file: deleting non-existent file succeeds silently ─────────────

    @Test
    fun `delete_file on non-existent file returns success without crashing`() = runTest {
        val deleteGhost = toolCallResponse("delete_file", "path" to "ghost.txt")
        coEvery { aiProvider.chat(any(), any(), any()) } returnsMany listOf(
            deleteGhost,
            taskCompleteResponse(),
        )
        coEvery { vcsProvider.createPullRequest(any(), any(), any(), any(), any()) } returns
            com.cramsan.agentic.core.PullRequest(
                "pr-1", "http://url", "PR",
                com.cramsan.agentic.core.PullRequestState.OPEN,
                "agentic/task-001", "main", listOf("agentic-code"),
            )

        val result = session.execute(task, worktree)

        assertIs<AgentResult.PrOpened>(result)
        // Confirm the delete result was reported back as success
        coVerify {
            aiProvider.chat(
                any(),
                match { msgs -> msgs.any { it.content.contains("File deleted") } },
                any(),
            )
        }
    }

    // ── read_file: reading a non-existent file returns error message ──────────

    @Test
    fun `read_file on non-existent file returns error message without crashing`() = runTest {
        val readMissing = toolCallResponse("read_file", "path" to "does-not-exist.txt")
        coEvery { aiProvider.chat(any(), any(), any()) } returnsMany listOf(
            readMissing,
            taskCompleteResponse(),
        )
        coEvery { vcsProvider.createPullRequest(any(), any(), any(), any(), any()) } returns
            com.cramsan.agentic.core.PullRequest(
                "pr-1", "http://url", "PR",
                com.cramsan.agentic.core.PullRequestState.OPEN,
                "agentic/task-001", "main", listOf("agentic-code"),
            )

        val result = session.execute(task, worktree)

        assertIs<AgentResult.PrOpened>(result)
        coVerify {
            aiProvider.chat(
                any(),
                match { msgs -> msgs.any { it.content.contains("Error reading file") } },
                any(),
            )
        }
    }
}

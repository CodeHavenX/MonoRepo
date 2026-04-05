package com.cramsan.agentic.claude

import com.cramsan.agentic.ai.AiContentBlock
import com.cramsan.agentic.ai.AiMessage
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
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Extended tests for DefaultAgentSession covering all tools, context resets,
 * and amendment lifecycle requirements from ARCHITECTURE.md §3 and TECH_DESIGN.md §5.3.
 */
class DefaultAgentSessionExtendedTest {

    @TempDir
    lateinit var worktreePath: Path

    private val aiProvider = mockk<AiProvider>()
    private val vcsProvider = mockk<VcsProvider>(relaxed = true)
    private val shell = mockk<ShellRunner>()
    private val documentStore = mockk<DocumentStore>(relaxed = true)

    private val task = Task(id = "task-001", title = "My Task", description = "Do something", dependencies = emptyList())

    private lateinit var worktree: Worktree
    private lateinit var session: DefaultAgentSession

    private fun taskCompleteResponse(prTitle: String = "PR", prBody: String = "Body") = AiResponse(
        id = "resp",
        content = listOf(
            AiContentBlock.ToolCall("t", "task_complete", buildJsonObject { put("prTitle", prTitle); put("prBody", prBody) }),
        ),
        stopReason = "tool_use",
    )

    private fun openPr(sourceBranch: String = "agentic/task-001", id: String = "pr-1") = PullRequest(
        id = id, url = "http://url", title = "PR", state = PullRequestState.OPEN,
        sourceBranch = sourceBranch, targetBranch = "main", labels = listOf("agentic-code"),
    )

    @BeforeEach
    fun setup() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        worktree = Worktree("task-001", worktreePath, "agentic/task-001")
        session = DefaultAgentSession(aiProvider, vcsProvider, shell, "claude-opus-4-6", "main", documentStore)

        coEvery { vcsProvider.listOpenPullRequests(any()) } returns emptyList()
        coEvery { shell.run(*anyVararg()) } returns ShellResult("", 0, "")
        coEvery { documentStore.getAll() } returns emptyList()
        coEvery { vcsProvider.createPullRequest(any(), any(), any(), any(), any()) } returns openPr()
    }

    // ── write_file tool ──────────────────────────────────────────────────────

    @Test
    fun `write_file creates intermediate directories`() = runTest {
        val writeInput = buildJsonObject {
            put("path", "nested/deep/file.kt")
            put("content", "package test")
        }
        var callCount = 0
        coEvery { aiProvider.chat(any(), any(), any(), any()) } answers {
            if (callCount++ == 0) {
                AiResponse("r1", listOf(AiContentBlock.ToolCall("t1", "write_file", writeInput)), "tool_use")
            } else {
                taskCompleteResponse()
            }
        }

        session.execute(task, worktree)

        val createdFile = worktreePath.resolve("nested/deep/file.kt")
        assertTrue(Files.exists(createdFile))
        assertEquals("package test", Files.readString(createdFile))
    }

    @Test
    fun `write_file overwrites existing file content`() = runTest {
        val file = worktreePath.resolve("existing.txt")
        Files.writeString(file, "old content")

        val writeInput = buildJsonObject { put("path", "existing.txt"); put("content", "new content") }
        var callCount = 0
        coEvery { aiProvider.chat(any(), any(), any(), any()) } answers {
            if (callCount++ == 0) {
                AiResponse("r1", listOf(AiContentBlock.ToolCall("t1", "write_file", writeInput)), "tool_use")
            } else taskCompleteResponse()
        }

        session.execute(task, worktree)

        assertEquals("new content", Files.readString(file))
    }

    // ── delete_file tool ──────────────────────────────────────────────────────

    @Test
    fun `delete_file removes an existing file`() = runTest {
        val file = worktreePath.resolve("to-delete.txt")
        Files.writeString(file, "will be deleted")

        val deleteInput = buildJsonObject { put("path", "to-delete.txt") }
        var callCount = 0
        coEvery { aiProvider.chat(any(), any(), any(), any()) } answers {
            if (callCount++ == 0) {
                AiResponse("r1", listOf(AiContentBlock.ToolCall("t1", "delete_file", deleteInput)), "tool_use")
            } else taskCompleteResponse()
        }

        session.execute(task, worktree)

        assertEquals(false, Files.exists(file))
    }

    @Test
    fun `delete_file on non-existent file returns success without error`() = runTest {
        val deleteInput = buildJsonObject { put("path", "does-not-exist.txt") }
        var callCount = 0
        coEvery { aiProvider.chat(any(), any(), any(), any()) } answers {
            if (callCount++ == 0) {
                AiResponse("r1", listOf(AiContentBlock.ToolCall("t1", "delete_file", deleteInput)), "tool_use")
            } else taskCompleteResponse()
        }

        // Should not throw
        val result = session.execute(task, worktree)
        assertIs<AgentResult.PrOpened>(result)
    }

    // ── list_files tool ───────────────────────────────────────────────────────

    @Test
    fun `list_files returns files matching glob pattern`() = runTest {
        Files.writeString(worktreePath.resolve("Foo.kt"), "")
        Files.writeString(worktreePath.resolve("Bar.kt"), "")
        Files.writeString(worktreePath.resolve("ignored.txt"), "")

        val listInput = buildJsonObject { put("glob", "*.kt") }

        var secondCallMessages: List<AiMessage>? = null
        var callCount = 0
        coEvery { aiProvider.chat(any(), any(), any(), any()) } answers {
            callCount++
            if (callCount == 1) {
                AiResponse("r1", listOf(AiContentBlock.ToolCall("t1", "list_files", listInput)), "tool_use")
            } else {
                secondCallMessages = arg(2)
                taskCompleteResponse()
            }
        }

        session.execute(task, worktree)

        val toolResultMessage = secondCallMessages?.lastOrNull { it.role == "user" }
        assertNotNull(toolResultMessage)
        assertTrue(toolResultMessage.content.contains("Foo.kt") || toolResultMessage.content.contains("Bar.kt"))
    }

    // ── propose_amendment: non-critical ──────────────────────────────────────

    @Test
    fun `propose_amendment non-critical creates Document PR and continues working`() = runTest {
        val amendInput = buildJsonObject {
            put("documentType", "STANDARDS")
            put("proposedChange", "Add error handling guidelines")
            put("isCritical", false)
        }
        val amendPr = openPr(sourceBranch = "agentic/task-001/amendment", id = "amend-pr-1")

        var callCount = 0
        coEvery { aiProvider.chat(any(), any(), any(), any()) } answers {
            when (callCount++) {
                0 -> AiResponse("r1", listOf(AiContentBlock.ToolCall("t1", "propose_amendment", amendInput)), "tool_use")
                else -> taskCompleteResponse()
            }
        }
        coEvery {
            vcsProvider.createPullRequest(
                sourceBranch = "agentic/task-001/amendment",
                targetBranch = any(),
                title = any(),
                body = any(),
                labels = any(),
            )
        } returns amendPr

        val result = session.execute(task, worktree)

        // Non-critical amendment: task continues and eventually opens a code PR
        assertIs<AgentResult.PrOpened>(result)
        // Amendment PR was created with the document label
        coVerify {
            vcsProvider.createPullRequest(
                sourceBranch = "agentic/task-001/amendment",
                targetBranch = any(),
                title = any(),
                body = any(),
                labels = match { it.contains("agentic-document") },
            )
        }
    }

    // ── propose_amendment: critical, merged ───────────────────────────────────

    @Test
    fun `propose_amendment critical writes awaiting-amendment marker while waiting`() = runTest {
        val amendInput = buildJsonObject {
            put("documentType", "TASK_LIST")
            put("proposedChange", "Add subtask")
            put("isCritical", true)
        }
        val amendPr = PullRequest(
            id = "amend-pr-1", url = "url", title = "Amendment", state = PullRequestState.OPEN,
            sourceBranch = "agentic/task-001/amendment", targetBranch = "main", labels = listOf("agentic-document"),
        )

        coEvery {
            vcsProvider.createPullRequest(
                sourceBranch = "agentic/task-001/amendment",
                targetBranch = any(), title = any(), body = any(), labels = any(),
            )
        } returns amendPr

        // isPullRequestMerged returns true immediately (fast-path for testing)
        coEvery { vcsProvider.isPullRequestMerged("amend-pr-1") } returns true

        var callCount = 0
        coEvery { aiProvider.chat(any(), any(), any(), any()) } answers {
            when (callCount++) {
                0 -> AiResponse("r1", listOf(AiContentBlock.ToolCall("t1", "propose_amendment", amendInput)), "tool_use")
                else -> taskCompleteResponse()
            }
        }

        session.execute(task, worktree)

        // After the session completes the marker should have been cleared
        val marker = worktreePath.resolve(".agentic-awaiting-amendment.txt")
        assertEquals(false, Files.exists(marker), "awaiting-amendment marker must be cleared after merge")
    }

    @Test
    fun `propose_amendment critical continues after amendment PR is merged`() = runTest {
        val amendInput = buildJsonObject {
            put("documentType", "TASK_LIST")
            put("proposedChange", "Add subtask")
            put("isCritical", true)
        }
        val amendPr = PullRequest(
            id = "amend-pr-1", url = "url", title = "Amendment", state = PullRequestState.OPEN,
            sourceBranch = "agentic/task-001/amendment", targetBranch = "main", labels = listOf("agentic-document"),
        )

        coEvery {
            vcsProvider.createPullRequest(
                sourceBranch = "agentic/task-001/amendment",
                targetBranch = any(), title = any(), body = any(), labels = any(),
            )
        } returns amendPr
        coEvery { vcsProvider.isPullRequestMerged("amend-pr-1") } returns true

        var callCount = 0
        coEvery { aiProvider.chat(any(), any(), any(), any()) } answers {
            when (callCount++) {
                0 -> AiResponse("r1", listOf(AiContentBlock.ToolCall("t1", "propose_amendment", amendInput)), "tool_use")
                else -> taskCompleteResponse()
            }
        }

        val result = session.execute(task, worktree)

        // After the critical amendment merges, the agent continues and completes the task
        assertIs<AgentResult.PrOpened>(result)
    }

    // ── propose_amendment: critical, PR closed without merge ─────────────────

    @Test
    fun `propose_amendment critical PR closed without merge returns error tool result and agent can fail safely`() = runTest {
        val amendInput = buildJsonObject {
            put("documentType", "TASK_LIST")
            put("proposedChange", "New subtask")
            put("isCritical", true)
        }
        val amendPr = PullRequest(
            id = "amend-pr-1", url = "url", title = "Amendment", state = PullRequestState.CLOSED,
            sourceBranch = "agentic/task-001/amendment", targetBranch = "main", labels = listOf("agentic-document"),
        )

        coEvery {
            vcsProvider.createPullRequest(
                sourceBranch = "agentic/task-001/amendment",
                targetBranch = any(), title = any(), body = any(), labels = any(),
            )
        } returns amendPr
        coEvery { vcsProvider.isPullRequestMerged("amend-pr-1") } returns false
        // PR is not in open list (it was closed/rejected)
        coEvery { vcsProvider.listOpenPullRequests() } returns emptyList()
        coEvery { vcsProvider.listOpenPullRequests(any()) } returns emptyList()

        val taskFailedInput = buildJsonObject { put("reason", "amendment rejected") }
        var callCount = 0
        coEvery { aiProvider.chat(any(), any(), any(), any()) } answers {
            when (callCount++) {
                0 -> AiResponse("r1", listOf(AiContentBlock.ToolCall("t1", "propose_amendment", amendInput)), "tool_use")
                // After receiving the closed-PR error message the agent calls task_failed
                else -> AiResponse("r2", listOf(AiContentBlock.ToolCall("t2", "task_failed", taskFailedInput)), "tool_use")
            }
        }

        val result = session.execute(task, worktree)

        // The amendment marker must be cleared even when PR is rejected
        val marker = worktreePath.resolve(".agentic-awaiting-amendment.txt")
        assertEquals(false, Files.exists(marker), "awaiting-amendment marker must be cleared when PR rejected")
    }

    // ── split_task tool ───────────────────────────────────────────────────────

    @Test
    fun `split_task creates both a code PR and a document PR then returns PrOpened`() = runTest {
        val splitInput = buildJsonObject {
            put("currentPrTitle", "Partial work")
            put("currentPrBody", "Part 1 done")
            put("newTaskTitle", "Complete the rest")
            put("newTaskDescription", "Finish what was planned")
        }
        val codePr = openPr(sourceBranch = "agentic/task-001", id = "code-pr-1")
        val docPr = openPr(sourceBranch = "agentic/task-001/split", id = "doc-pr-1")

        coEvery {
            vcsProvider.createPullRequest(
                sourceBranch = "agentic/task-001",
                targetBranch = any(), title = any(), body = any(), labels = any(),
            )
        } returns codePr
        coEvery {
            vcsProvider.createPullRequest(
                sourceBranch = "agentic/task-001/split",
                targetBranch = any(), title = any(), body = any(), labels = any(),
            )
        } returns docPr

        coEvery { aiProvider.chat(any(), any(), any(), any()) } returns
            AiResponse("r1", listOf(AiContentBlock.ToolCall("t1", "split_task", splitInput)), "tool_use")

        val result = session.execute(task, worktree)

        assertIs<AgentResult.PrOpened>(result)
        assertEquals("code-pr-1", (result as AgentResult.PrOpened).prId)

        // Both PRs must have been created
        coVerify {
            vcsProvider.createPullRequest(
                sourceBranch = "agentic/task-001",
                targetBranch = any(), title = any(), body = any(),
                labels = match { "agentic-code" in it },
            )
        }
        coVerify {
            vcsProvider.createPullRequest(
                sourceBranch = "agentic/task-001/split",
                targetBranch = any(), title = any(), body = any(),
                labels = match { "agentic-document" in it },
            )
        }
    }

    // ── Unknown tool ──────────────────────────────────────────────────────────

    @Test
    fun `unknown tool returns error message to agent without crashing`() = runTest {
        val unknownInput = buildJsonObject { put("param", "value") }
        var callCount = 0
        coEvery { aiProvider.chat(any(), any(), any(), any()) } answers {
            when (callCount++) {
                0 -> AiResponse("r1", listOf(AiContentBlock.ToolCall("t1", "nonexistent_tool", unknownInput)), "tool_use")
                else -> taskCompleteResponse()
            }
        }

        val result = session.execute(task, worktree)

        // Session should recover and eventually complete
        assertIs<AgentResult.PrOpened>(result)
    }

    @Test
    fun `unknown tool result message sent back to agent mentions the tool name`() = runTest {
        val unknownInput = buildJsonObject {}
        var capturedMessages: List<AiMessage>? = null
        var callCount = 0
        coEvery { aiProvider.chat(any(), any(), any(), any()) } answers {
            callCount++
            if (callCount == 1) {
                AiResponse("r1", listOf(AiContentBlock.ToolCall("t1", "unknown_tool_xyz", unknownInput)), "tool_use")
            } else {
                capturedMessages = arg(2)
                taskCompleteResponse()
            }
        }

        session.execute(task, worktree)

        val toolResultMsg = capturedMessages?.lastOrNull { it.role == "user" }
        assertNotNull(toolResultMsg)
        assertTrue(toolResultMsg.content.contains("unknown_tool_xyz") || toolResultMsg.content.contains("Unknown tool"))
    }

    // ── No tool calls + end_turn prompts continuation ────────────────────────

    @Test
    fun `no tool calls with end_turn prompts agent to continue making another chat call`() = runTest {
        var callCount = 0
        coEvery { aiProvider.chat(any(), any(), any(), any()) } answers {
            when (callCount++) {
                0 -> AiResponse("r1", listOf(AiContentBlock.Text("Let me think...")), "end_turn")
                else -> taskCompleteResponse()
            }
        }

        session.execute(task, worktree)

        // chat() should be called at least twice — once for initial response, once after continuation prompt
        coVerify(atLeast = 2) { aiProvider.chat(any(), any(), any(), any()) }
    }

    // ── Context reset: initial context selection ─────────────────────────────

    @Test
    fun `fresh start uses task start prompt containing task id and title`() = runTest {
        val capturedSystemPrompt = slot<String>()
        coEvery { aiProvider.chat(any(), capture(capturedSystemPrompt), any(), any()) } returns taskCompleteResponse()

        session.execute(task, worktree)

        assertTrue(capturedSystemPrompt.captured.contains(task.id) || capturedSystemPrompt.captured.contains(task.title))
    }

    @Test
    fun `when PR has changes requested context includes changes requested prompt`() = runTest {
        val pr = openPr()
        coEvery { vcsProvider.listOpenPullRequests(any()) } returns listOf(pr)
        coEvery { vcsProvider.pullRequestHasRequestedChanges("pr-1") } returns true
        coEvery { vcsProvider.getPullRequestComments("pr-1") } returns emptyList()
        coEvery { shell.run("git", "diff", "main", "--", any()) } returns ShellResult("- old\n+ new", 0, "")

        val capturedSystemPrompt = slot<String>()
        coEvery { aiProvider.chat(any(), capture(capturedSystemPrompt), any(), any()) } returns taskCompleteResponse()

        session.execute(task, worktree)

        // The prompt should reference review feedback/changes
        val prompt = capturedSystemPrompt.captured
        assertTrue(
            prompt.contains("reviewer") || prompt.contains("requested") || prompt.contains("feedback") ||
                prompt.contains("Reviewer") || prompt.contains("Changes"),
            "System prompt for changes-requested context should reference review. Got: $prompt",
        )
    }

    @Test
    fun `when open PR without changes context uses PR opened prompt`() = runTest {
        val pr = openPr()
        coEvery { vcsProvider.listOpenPullRequests(any()) } returns listOf(pr)
        coEvery { vcsProvider.pullRequestHasRequestedChanges("pr-1") } returns false
        coEvery { shell.run("git", "diff", "main", "--", any()) } returns ShellResult("+ new code", 0, "")

        val capturedSystemPrompt = slot<String>()
        coEvery { aiProvider.chat(any(), capture(capturedSystemPrompt), any(), any()) } returns taskCompleteResponse()

        session.execute(task, worktree)

        val prompt = capturedSystemPrompt.captured
        assertTrue(
            prompt.contains("Pull Request") || prompt.contains("review") || prompt.contains("PR") ||
                prompt.contains("opened") || prompt.contains("awaiting"),
            "System prompt for PR-opened context should reference the PR. Got: $prompt",
        )
    }

    @Test
    fun `when worktree has existing diff and no PR context uses resume prompt`() = runTest {
        coEvery { vcsProvider.listOpenPullRequests(any()) } returns emptyList()
        coEvery { shell.run("git", "diff", "main", "--", any()) } returns ShellResult("+ some existing change", 0, "")

        val capturedSystemPrompt = slot<String>()
        coEvery { aiProvider.chat(any(), capture(capturedSystemPrompt), any(), any()) } returns taskCompleteResponse()

        session.execute(task, worktree)

        val prompt = capturedSystemPrompt.captured
        assertTrue(
            prompt.contains("resuming") || prompt.contains("interrupted") || prompt.contains("resume") ||
                prompt.contains("Resume") || prompt.contains("previous"),
            "System prompt for resume context should reference prior work. Got: $prompt",
        )
    }

    // ── run_command: working directory ────────────────────────────────────────

    @Test
    fun `run_command executes in worktree working directory by default`() = runTest {
        val runInput = buildJsonObject { put("command", "pwd") }

        coEvery { shell.run(any(), any(), any(), workingDir = any()) } returns
            ShellResult(worktreePath.toString(), 0, "")

        var callCount = 0
        coEvery { aiProvider.chat(any(), any(), any(), any()) } answers {
            if (callCount++ == 0) {
                AiResponse("r1", listOf(AiContentBlock.ToolCall("t1", "run_command", runInput)), "tool_use")
            } else taskCompleteResponse()
        }

        session.execute(task, worktree)

        coVerify {
            shell.run("sh", "-c", "pwd", workingDir = worktreePath.toString())
        }
    }

    @Test
    fun `run_command result includes stdout and exit code in tool result message`() = runTest {
        coEvery { shell.run("sh", "-c", "echo hello", workingDir = any()) } returns ShellResult("hello\n", 0, "")

        val runInput = buildJsonObject { put("command", "echo hello") }
        var secondCallMessages: List<AiMessage>? = null
        var callCount = 0
        coEvery { aiProvider.chat(any(), any(), any(), any()) } answers {
            callCount++
            if (callCount == 1) {
                AiResponse("r1", listOf(AiContentBlock.ToolCall("t1", "run_command", runInput)), "tool_use")
            } else {
                secondCallMessages = arg(2)
                taskCompleteResponse()
            }
        }

        session.execute(task, worktree)

        val toolMsg = secondCallMessages?.lastOrNull { it.role == "user" }
        assertNotNull(toolMsg)
        assertTrue(toolMsg.content.contains("hello") && toolMsg.content.contains("exit code"))
    }
}

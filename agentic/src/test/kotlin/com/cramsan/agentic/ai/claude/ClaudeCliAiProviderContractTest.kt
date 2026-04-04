package com.cramsan.agentic.ai.claude

import com.cramsan.agentic.ai.AiMessage
import com.cramsan.agentic.ai.AiProviderException
import com.cramsan.agentic.ai.AiTool
import com.cramsan.agentic.vcs.github.ShellResult
import com.cramsan.agentic.vcs.github.ShellRunner
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.buildJsonObject
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * Contract tests for ClaudeCliAiProvider enforcing requirements from
 * AMENDMENT_CLAUDE_CLI_PROVIDER.md.
 */
class ClaudeCliAiProviderContractTest {

    private val shell = mockk<ShellRunner>()
    private val provider = ClaudeCliAiProvider(shell, cliPath = "claude")

    // ── CLI invocation correctness ────────────────────────────────────────────

    @Test
    fun `chat invokes claude CLI with --print and --model flags`() = runTest {
        coEvery { shell.run(*anyVararg()) } returns ShellResult("response text", 0, "")

        provider.chat("claude-opus-4-6", "sys", listOf(AiMessage("user", "hi")), emptyList())

        coVerify {
            shell.run(
                "claude",
                "--print",
                "--model", "claude-opus-4-6",
                any(),
            )
        }
    }

    @Test
    fun `chat uses the configured cliPath binary`() = runTest {
        val customCli = ClaudeCliAiProvider(shell, cliPath = "/opt/homebrew/bin/claude")
        coEvery { shell.run(*anyVararg()) } returns ShellResult("ok", 0, "")

        customCli.chat("claude-opus-4-6", "sys", listOf(AiMessage("user", "hi")), emptyList())

        coVerify { shell.run("/opt/homebrew/bin/claude", any(), any(), any(), any()) }
    }

    // ── Conversation collapsing format ────────────────────────────────────────

    @Test
    fun `collapse starts with system section`() {
        val collapsed = provider.collapseConversation("You are helpful.", emptyList())
        assertTrue(collapsed.startsWith("[System]"), "Collapsed prompt must start with [System]. Got: $collapsed")
    }

    @Test
    fun `collapse renders user message with User label`() {
        val collapsed = provider.collapseConversation("sys", listOf(AiMessage("user", "Hello!")))
        assertTrue(collapsed.contains("[User]"), "Must contain [User] label. Got: $collapsed")
        assertTrue(collapsed.contains("Hello!"), "Must contain user message content. Got: $collapsed")
    }

    @Test
    fun `collapse renders assistant message with Assistant label`() {
        val collapsed = provider.collapseConversation(
            "sys",
            listOf(AiMessage("user", "Hi"), AiMessage("assistant", "Hello back!")),
        )
        assertTrue(collapsed.contains("[Assistant]"), "Must contain [Assistant] label. Got: $collapsed")
        assertTrue(collapsed.contains("Hello back!"), "Must contain assistant content. Got: $collapsed")
    }

    @Test
    fun `collapse preserves turn order`() {
        val collapsed = provider.collapseConversation(
            "sys",
            listOf(
                AiMessage("user", "FIRST"),
                AiMessage("assistant", "SECOND"),
                AiMessage("user", "THIRD"),
            ),
        )
        val firstIdx = collapsed.indexOf("FIRST")
        val secondIdx = collapsed.indexOf("SECOND")
        val thirdIdx = collapsed.indexOf("THIRD")
        assertTrue(firstIdx < secondIdx, "FIRST must appear before SECOND")
        assertTrue(secondIdx < thirdIdx, "SECOND must appear before THIRD")
    }

    @Test
    fun `collapse with no messages contains only system section`() {
        val collapsed = provider.collapseConversation("System prompt only.", emptyList())
        assertEquals("[System]\nSystem prompt only.", collapsed)
    }

    @Test
    fun `collapse passes the full text as a single CLI argument`() = runTest {
        coEvery { shell.run(*anyVararg()) } returns ShellResult("answer", 0, "")

        provider.chat(
            model = "claude-opus-4-6",
            systemPrompt = "You are helpful",
            messages = listOf(AiMessage("user", "Unique marker 12345")),
            tools = emptyList(),
        )

        // The last argument passed to shell.run must contain the user message content
        coVerify {
            shell.run(
                any(), any(), any(), any(),
                match { it.contains("Unique marker 12345") },
            )
        }
    }

    // ── Tool use rejection (agent-session incompatibility) ───────────────────

    @Test
    fun `chat with single tool throws UnsupportedOperationException`() = runTest {
        val tool = AiTool("read_file", "Reads a file", buildJsonObject {})
        assertFailsWith<UnsupportedOperationException> {
            provider.chat("claude-opus-4-6", "sys", listOf(AiMessage("user", "hi")), listOf(tool))
        }
    }

    @Test
    fun `chat with multiple tools throws UnsupportedOperationException`() = runTest {
        val tools = listOf(
            AiTool("read_file", "Reads", buildJsonObject {}),
            AiTool("write_file", "Writes", buildJsonObject {}),
        )
        assertFailsWith<UnsupportedOperationException> {
            provider.chat("claude-opus-4-6", "sys", listOf(AiMessage("user", "hi")), tools)
        }
    }

    @Test
    fun `UnsupportedOperationException message explains the limitation`() = runTest {
        val tool = AiTool("task_complete", "Completes task", buildJsonObject {})
        val ex = runCatching {
            provider.chat("model", "sys", listOf(AiMessage("user", "hi")), listOf(tool))
        }.exceptionOrNull()
        assertTrue(ex is UnsupportedOperationException)
        val msg = ex.message ?: ""
        assertTrue(
            msg.contains("tool") || msg.contains("CLI") || msg.contains("not supported"),
            "Exception message should explain the limitation. Got: $msg",
        )
    }

    // ── Error handling ────────────────────────────────────────────────────────

    @Test
    fun `exit code 1 throws AiProviderException with exit code 1`() = runTest {
        coEvery { shell.run(*anyVararg()) } returns ShellResult("", 1, "auth failure")

        val ex = assertFailsWith<AiProviderException> {
            provider.chat("claude-opus-4-6", "sys", listOf(AiMessage("user", "hi")), emptyList())
        }
        assertEquals(1, ex.exitCode)
    }

    @Test
    fun `exit code 127 (command not found) throws AiProviderException with exit code 127`() = runTest {
        coEvery { shell.run(*anyVararg()) } returns ShellResult("claude: command not found", 127, "")

        val ex = assertFailsWith<AiProviderException> {
            provider.chat("claude-opus-4-6", "sys", listOf(AiMessage("user", "hi")), emptyList())
        }
        assertEquals(127, ex.exitCode)
    }

    @Test
    fun `AiProviderException message contains stderr when non-blank`() = runTest {
        coEvery { shell.run(*anyVararg()) } returns ShellResult("", 1, "rate limit exceeded")

        val ex = assertFailsWith<AiProviderException> {
            provider.chat("claude-opus-4-6", "sys", listOf(AiMessage("user", "hi")), emptyList())
        }
        assertTrue(ex.message!!.contains("rate limit exceeded"))
    }

    @Test
    fun `stdout is trimmed before wrapping in AiResponse`() = runTest {
        coEvery { shell.run(*anyVararg()) } returns ShellResult("  response with whitespace  \n", 0, "")

        val response = provider.chat("model", "sys", listOf(AiMessage("user", "hi")), emptyList())

        val textBlock = response.content.filterIsInstance<com.cramsan.agentic.ai.AiContentBlock.Text>().first()
        assertEquals("response with whitespace", textBlock.text)
    }
}

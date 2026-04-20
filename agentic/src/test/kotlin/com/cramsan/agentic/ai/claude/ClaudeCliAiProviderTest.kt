package com.cramsan.agentic.ai.claude

import com.cramsan.agentic.ai.AiContentBlock
import com.cramsan.agentic.ai.AiMessage
import com.cramsan.agentic.ai.AiProviderException
import com.cramsan.agentic.ai.AiTool
import com.cramsan.agentic.vcs.github.ShellResult
import com.cramsan.agentic.vcs.github.ShellRunner
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertTrue

class ClaudeCliAiProviderTest {

    @BeforeEach
    fun setup() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
    }

    private val shell = mockk<ShellRunner>()
    private val provider = ClaudeCliAiProvider(shell, cliPath = "claude", model = "claude-opus-4-6")

    @Test
    fun `successful response is wrapped in AiResponse with Text block`() = runTest {
        coEvery { shell.run(*anyVararg()) } returns ShellResult(
            stdout = "  Hello from CLI  ",
            exitCode = 0,
            stderr = "",
        )

        val response = provider.chat(
            systemPrompt = "You are helpful",
            messages = listOf(AiMessage("user", "Hello")),
            tools = emptyList(),
        )

        assertEquals(1, response.content.size)
        assertIs<AiContentBlock.Text>(response.content[0])
        assertEquals("Hello from CLI", (response.content[0] as AiContentBlock.Text).text)
        assertEquals("end_turn", response.stopReason)
        assertTrue(response.id.startsWith("cli-"))
    }

    @Test
    fun `non-zero exit code throws AiProviderException`() = runTest {
        coEvery { shell.run(*anyVararg()) } returns ShellResult(
            stdout = "",
            exitCode = 1,
            stderr = "authentication error",
        )

        val ex = assertFailsWith<AiProviderException> {
            provider.chat(
                systemPrompt = "sys",
                messages = listOf(AiMessage("user", "hi")),
                tools = emptyList(),
            )
        }

        assertEquals(1, ex.exitCode)
        assertTrue(ex.message!!.contains("authentication error"))
    }

    @Test
    fun `non-empty tools list throws UnsupportedOperationException`() = runTest {
        val dummyTool = AiTool("my_tool", "does something", buildJsonObject {})

        assertFailsWith<UnsupportedOperationException> {
            provider.chat(
                systemPrompt = "sys",
                messages = listOf(AiMessage("user", "hi")),
                tools = listOf(dummyTool),
            )
        }
    }

    @Test
    fun `collapseConversation includes system prompt and all turns with role labels`() {
        val collapsed = provider.collapseConversation(
            systemPrompt = "You are helpful",
            messages = listOf(
                AiMessage("user", "Hello"),
                AiMessage("assistant", "Hi there"),
                AiMessage("user", "How are you?"),
            ),
        )

        assertTrue(collapsed.contains("[System]"))
        assertTrue(collapsed.contains("You are helpful"))
        assertTrue(collapsed.contains("[User]"))
        assertTrue(collapsed.contains("Hello"))
        assertTrue(collapsed.contains("[Assistant]"))
        assertTrue(collapsed.contains("Hi there"))
        assertTrue(collapsed.contains("How are you?"))
    }

    @Test
    fun `collapseConversation with single user message produces correct format`() {
        val collapsed = provider.collapseConversation(
            systemPrompt = "sys",
            messages = listOf(AiMessage("user", "question")),
        )

        val expected = "[System]\nsys\n\n[User]\nquestion"
        assertEquals(expected, collapsed)
    }

    @Test
    fun `non-zero exit code uses stdout when stderr is blank`() = runTest {
        coEvery { shell.run(*anyVararg()) } returns ShellResult(
            stdout = "command not found",
            exitCode = 127,
            stderr = "",
        )

        val ex = assertFailsWith<AiProviderException> {
            provider.chat("sys", listOf(AiMessage("user", "hi")), emptyList())
        }

        assertEquals(127, ex.exitCode)
        assertTrue(ex.message!!.contains("command not found"))
    }

    // ── fullAccess=true mode ──────────────────────────────────────────────────

    private val jsonSerializer = Json { ignoreUnknownKeys = true }
    private val fullProvider = ClaudeCliAiProvider(
        shell = shell,
        cliPath = "claude",
        model = "claude-opus-4-6",
        fullAccess = true,
        json = jsonSerializer,
    )

    @Test
    fun `fullAccess=true invokes CLI with --dangerously-skip-permissions and --output-format json`() = runTest {
        coEvery { shell.run(*anyVararg()) } returns ShellResult(
            stdout = """{"type":"result","subtype":"success","is_error":false,"result":"Done"}""",
            exitCode = 0,
            stderr = "",
        )

        fullProvider.chat("sys", listOf(AiMessage("user", "hi")), emptyList())

        coVerify {
            shell.run(
                "claude",
                "--print",
                "--output-format", "json",
                "--dangerously-skip-permissions",
                "--model", "claude-opus-4-6",
                any(),
            )
        }
    }

    @Test
    fun `fullAccess=true parses result field from JSON output`() = runTest {
        coEvery { shell.run(*anyVararg()) } returns ShellResult(
            stdout = """{"type":"result","subtype":"success","is_error":false,"result":"Task complete"}""",
            exitCode = 0,
            stderr = "",
        )

        val response = fullProvider.chat("sys", listOf(AiMessage("user", "go")), emptyList())

        val text = (response.content[0] as AiContentBlock.Text).text
        assertEquals("Task complete", text)
    }

    @Test
    fun `fullAccess=true is_error=true throws AiProviderException`() = runTest {
        coEvery { shell.run(*anyVararg()) } returns ShellResult(
            stdout = """{"type":"result","subtype":"error","is_error":true,"result":"out of context"}""",
            exitCode = 0,
            stderr = "",
        )

        assertFailsWith<AiProviderException> {
            fullProvider.chat("sys", listOf(AiMessage("user", "go")), emptyList())
        }
    }

    @Test
    fun `fullAccess=true collapseConversationWithTools includes Available Tools section`() {
        val tool = AiTool("bash_run", "Runs bash commands", buildJsonObject {})
        val collapsed = fullProvider.collapseConversationWithTools("sys", listOf(AiMessage("user", "hi")), listOf(tool))

        assertTrue(collapsed.contains("[Available Tools]"))
        assertTrue(collapsed.contains("bash_run"))
        assertTrue(collapsed.contains("Runs bash commands"))
    }

    @Test
    fun `fullAccess=true does not throw on non-empty tools`() = runTest {
        coEvery { shell.run(*anyVararg()) } returns ShellResult(
            stdout = """{"type":"result","is_error":false,"result":"ok"}""",
            exitCode = 0,
            stderr = "",
        )
        val tool = AiTool("write_file", "Writes a file", buildJsonObject {})

        fullProvider.chat("sys", listOf(AiMessage("user", "do it")), listOf(tool))
    }
}

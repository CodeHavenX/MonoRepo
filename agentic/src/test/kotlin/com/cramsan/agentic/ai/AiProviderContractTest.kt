package com.cramsan.agentic.ai

import com.cramsan.agentic.ai.claude.ClaudeCliAiProvider
import com.cramsan.agentic.vcs.github.ShellResult
import com.cramsan.agentic.vcs.github.ShellRunner
import io.mockk.coEvery
import io.mockk.mockk
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.buildJsonObject
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Cross-cutting contract tests for the AiProvider abstraction enforcing the
 * interface contract defined in AMENDMENT_AI_PROVIDER_ABSTRACTION.md.
 *
 * These tests ensure every AiProvider implementation (current and future) satisfies
 * the common behavioral requirements of the interface.
 */
class AiProviderContractTest {

    private val shell = mockk<ShellRunner>()

    @BeforeEach
    fun setup() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
    }

    // ── AiProviderException carries an exit code ──────────────────────────────

    @Test
    fun `AiProviderException stores provided exit code`() {
        val ex = AiProviderException("something failed", 42)
        assert(ex.exitCode == 42)
    }

    @Test
    fun `AiProviderException default exit code is -1 when not specified`() {
        val ex = AiProviderException("generic failure")
        assert(ex.exitCode == -1)
    }

    @Test
    fun `AiProviderException message is preserved`() {
        val ex = AiProviderException("rate limit exceeded", 429)
        assertTrue(ex.message!!.contains("rate limit exceeded"))
    }

    // ── AiResponse structure ──────────────────────────────────────────────────

    @Test
    fun `AiResponse contains id content list and stopReason`() {
        val response = AiResponse(
            id = "resp-001",
            content = listOf(AiContentBlock.Text("hello")),
            stopReason = "end_turn",
        )
        assertNotNull(response.id)
        assertTrue(response.content.isNotEmpty())
        assertNotNull(response.stopReason)
    }

    @Test
    fun `AiContentBlock Text carries text property`() {
        val block = AiContentBlock.Text("some text output")
        assertIs<AiContentBlock.Text>(block)
        assertTrue(block.text.contains("some text output"))
    }

    @Test
    fun `AiContentBlock ToolCall carries id name and input`() {
        val input = buildJsonObject { }
        val block = AiContentBlock.ToolCall(id = "tc-1", name = "read_file", input = input)
        assertIs<AiContentBlock.ToolCall>(block)
        assert(block.id == "tc-1")
        assert(block.name == "read_file")
    }

    // ── ClaudeCliAiProvider is NOT compatible with tool-use workloads ─────────

    @Test
    fun `ClaudeCliAiProvider throws UnsupportedOperationException on any non-empty tools list`() = runTest {
        val provider = ClaudeCliAiProvider(shell)
        val anyTool = AiTool("some_tool", "description", buildJsonObject {})

        assertFailsWith<UnsupportedOperationException> {
            provider.chat(
                systemPrompt = "sys",
                messages = listOf(AiMessage("user", "hi")),
                tools = listOf(anyTool),
            )
        }
    }

    @Test
    fun `ClaudeCliAiProvider succeeds with empty tools list`() = runTest {
        val provider = ClaudeCliAiProvider(shell)
        coEvery { shell.run(*anyVararg()) } returns ShellResult("plain text response", 0, "")

        val response = provider.chat(
            systemPrompt = "sys",
            messages = listOf(AiMessage("user", "hi")),
            tools = emptyList(),
        )

        assertNotNull(response)
        assertTrue(response.content.isNotEmpty())
    }

    // ── AiProvider response always has an id ─────────────────────────────────

    @Test
    fun `ClaudeCliAiProvider response id starts with cli-`() = runTest {
        val provider = ClaudeCliAiProvider(shell)
        coEvery { shell.run(*anyVararg()) } returns ShellResult("output", 0, "")

        val response = provider.chat("sys", listOf(AiMessage("user", "q")), emptyList())

        assertTrue(response.id.startsWith("cli-"), "CLI provider id must start with 'cli-'. Got: ${response.id}")
    }

    @Test
    fun `ClaudeCliAiProvider response stopReason is end_turn`() = runTest {
        val provider = ClaudeCliAiProvider(shell)
        coEvery { shell.run(*anyVararg()) } returns ShellResult("output", 0, "")

        val response = provider.chat("sys", listOf(AiMessage("user", "q")), emptyList())

        assert(response.stopReason == "end_turn")
    }
}

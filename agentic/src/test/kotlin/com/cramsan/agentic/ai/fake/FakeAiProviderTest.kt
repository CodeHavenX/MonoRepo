package com.cramsan.agentic.ai.fake

import com.cramsan.agentic.ai.AiContentBlock
import com.cramsan.agentic.ai.AiMessage
import com.cramsan.agentic.ai.AiResponse
import com.cramsan.agentic.ai.AiTool
import com.cramsan.agentic.core.FakeMode
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FakeAiProviderTest {

    @BeforeEach
    fun setup() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
    }

    // ── TEST mode tests ───────────────────────────────────────────────────────

    @Test
    fun `TEST mode returns queued response`() = runTest {
        val provider = FakeAiProvider(mode = FakeMode.TEST)
        val expectedResponse = AiResponse(
            id = "test-1",
            content = listOf(AiContentBlock.Text("Hello")),
            stopReason = "end_turn",
        )
        provider.enqueueResponse(expectedResponse)

        val result = provider.chat("system", emptyList(), emptyList())

        assertEquals(expectedResponse, result)
    }

    @Test
    fun `TEST mode throws when queue is empty`() = runTest {
        val provider = FakeAiProvider(mode = FakeMode.TEST)

        assertFailsWith<IllegalStateException> {
            provider.chat("system", emptyList(), emptyList())
        }
    }

    @Test
    fun `TEST mode returns responses in FIFO order`() = runTest {
        val provider = FakeAiProvider(mode = FakeMode.TEST)
        val first = provider.enqueueTextResponse("First")
        val second = provider.enqueueTextResponse("Second")

        val result1 = provider.chat("system", emptyList(), emptyList())
        val result2 = provider.chat("system", emptyList(), emptyList())

        assertEquals(first.content, result1.content)
        assertEquals(second.content, result2.content)
    }

    @Test
    fun `TEST mode captures requests`() = runTest {
        val provider = FakeAiProvider(mode = FakeMode.TEST)
        provider.enqueueTextResponse("response")
        val messages = listOf(AiMessage("user", "Hi"))
        val tools = listOf(AiTool("test_tool", "A test tool", buildJsonObject {}))

        provider.chat("Be helpful", messages, tools)

        val captured = provider.getLastRequest()
        assertNotNull(captured)
        assertEquals("Be helpful", captured.systemPrompt)
        assertEquals(messages, captured.messages)
        assertEquals(tools, captured.tools)
    }

    // ── DEMO mode tests ───────────────────────────────────────────────────────

    @Test
    fun `DEMO mode generates response when queue empty`() = runTest {
        val provider = FakeAiProvider(mode = FakeMode.DEMO)

        val result = provider.chat("system", listOf(AiMessage("user", "Hello")), emptyList())

        assertNotNull(result)
        assertTrue(result.content.isNotEmpty())
        assertTrue(result.id.startsWith("fake-"))
    }

    @Test
    fun `DEMO mode prefers queued response over generation`() = runTest {
        val provider = FakeAiProvider(mode = FakeMode.DEMO)
        val queued = provider.enqueueTextResponse("Queued!")

        val result = provider.chat("system", emptyList(), emptyList())

        assertEquals(queued.content, result.content)
    }

    @Test
    fun `DEMO mode auto-completes after configured turns`() = runTest {
        val provider = FakeAiProvider(mode = FakeMode.DEMO, autoCompleteAfterTurns = 2)
        val taskCompleteTool = AiTool(
            "task_complete",
            "Complete the task",
            buildJsonObject { put("type", "object") },
        )
        val tools = listOf(taskCompleteTool)

        // First two calls should not auto-complete
        provider.chat("system", listOf(AiMessage("user", "Continue")), tools)
        provider.chat("system", listOf(AiMessage("user", "Continue")), tools)

        // Third call should auto-complete
        val thirdResponse = provider.chat("system", listOf(AiMessage("user", "Continue")), tools)
        val toolCall = thirdResponse.content.filterIsInstance<AiContentBlock.ToolCall>().firstOrNull()

        assertNotNull(toolCall)
        assertEquals("task_complete", toolCall.name)
    }

    @Test
    fun `DEMO mode does not auto-complete if task_complete tool not available`() = runTest {
        val provider = FakeAiProvider(mode = FakeMode.DEMO, autoCompleteAfterTurns = 1)

        // No task_complete tool provided
        val result = provider.chat("system", listOf(AiMessage("user", "Continue")), emptyList())

        // Should return text response, not tool call
        val textContent = result.content.filterIsInstance<AiContentBlock.Text>().firstOrNull()
        assertNotNull(textContent)
    }

    @Test
    fun `DEMO mode uses default text response`() = runTest {
        val customDefault = "Custom default response"
        val provider = FakeAiProvider(mode = FakeMode.DEMO, defaultTextResponse = customDefault)

        val result = provider.chat("system", listOf(AiMessage("user", "Hello")), emptyList())

        val textContent = result.content.filterIsInstance<AiContentBlock.Text>().firstOrNull()
        assertEquals(customDefault, textContent?.text)
    }

    // ── Pattern matching tests (DEMO mode) ────────────────────────────────────

    @Test
    fun `DEMO mode selects list_files tool for begin message`() = runTest {
        val provider = FakeAiProvider(mode = FakeMode.DEMO, autoCompleteAfterTurns = 100)
        val listFilesTool = AiTool("list_files", "List files", buildJsonObject {})

        val result = provider.chat(
            "system",
            listOf(AiMessage("user", "Begin working on this task")),
            listOf(listFilesTool),
        )

        val toolCall = result.content.filterIsInstance<AiContentBlock.ToolCall>().firstOrNull()
        assertNotNull(toolCall)
        assertEquals("list_files", toolCall.name)
    }

    @Test
    fun `DEMO mode selects read_file tool for read message`() = runTest {
        val provider = FakeAiProvider(mode = FakeMode.DEMO, autoCompleteAfterTurns = 100)
        val readFileTool = AiTool("read_file", "Read a file", buildJsonObject {})

        val result = provider.chat(
            "system",
            listOf(AiMessage("user", "Please read the configuration file")),
            listOf(readFileTool),
        )

        val toolCall = result.content.filterIsInstance<AiContentBlock.ToolCall>().firstOrNull()
        assertNotNull(toolCall)
        assertEquals("read_file", toolCall.name)
    }

    // ── Tool call convenience tests ───────────────────────────────────────────

    @Test
    fun `enqueueToolCallResponse creates correct structure`() = runTest {
        val provider = FakeAiProvider(mode = FakeMode.TEST)
        val input = buildJsonObject { put("path", "test.txt") }
        provider.enqueueToolCallResponse("read_file", input)

        val result = provider.chat("system", emptyList(), emptyList())

        val toolCall = result.content.filterIsInstance<AiContentBlock.ToolCall>().first()
        assertEquals("read_file", toolCall.name)
        assertEquals(input, toolCall.input)
        assertEquals("tool_use", result.stopReason)
    }

    @Test
    fun `enqueueTextResponse creates correct structure`() = runTest {
        val provider = FakeAiProvider(mode = FakeMode.TEST)
        val response = provider.enqueueTextResponse("Hello world", "stop")

        val result = provider.chat("system", emptyList(), emptyList())

        assertEquals("Hello world", (result.content.first() as AiContentBlock.Text).text)
        assertEquals("stop", result.stopReason)
        assertTrue(response.id.startsWith("fake-"))
    }

    // ── State management tests ────────────────────────────────────────────────

    @Test
    fun `reset clears all state`() = runTest {
        val provider = FakeAiProvider(mode = FakeMode.DEMO)
        provider.enqueueTextResponse("queued")
        provider.chat("system", emptyList(), emptyList())

        provider.reset()

        assertEquals(0, provider.queueSize())
        assertTrue(provider.getCapturedRequests().isEmpty())
    }

    @Test
    fun `clearQueue only clears queue not captured requests`() = runTest {
        val provider = FakeAiProvider(mode = FakeMode.DEMO)
        provider.enqueueTextResponse("queued1")
        provider.enqueueTextResponse("queued2")
        provider.chat("system", emptyList(), emptyList())

        provider.clearQueue()

        assertEquals(0, provider.queueSize())
        assertEquals(1, provider.getCapturedRequests().size)
    }

    @Test
    fun `clearCapturedRequests only clears requests not queue`() = runTest {
        val provider = FakeAiProvider(mode = FakeMode.DEMO)
        provider.enqueueTextResponse("queued")
        provider.chat("system", emptyList(), emptyList())
        provider.enqueueTextResponse("another")

        provider.clearCapturedRequests()

        assertEquals(1, provider.queueSize())
        assertTrue(provider.getCapturedRequests().isEmpty())
    }

    @Test
    fun `queueSize returns correct count`() {
        val provider = FakeAiProvider(mode = FakeMode.TEST)
        provider.enqueueTextResponse("1")
        provider.enqueueTextResponse("2")
        provider.enqueueTextResponse("3")

        assertEquals(3, provider.queueSize())
    }

    @Test
    fun `getCapturedRequests returns empty list initially`() {
        val provider = FakeAiProvider(mode = FakeMode.TEST)

        assertTrue(provider.getCapturedRequests().isEmpty())
    }

    @Test
    fun `getLastRequest returns null when no requests captured`() {
        val provider = FakeAiProvider(mode = FakeMode.TEST)

        assertNull(provider.getLastRequest())
    }

    // ── Delay simulation test ─────────────────────────────────────────────────

    @Test
    fun `delay configuration is accepted`() = runTest {
        // Note: runTest uses virtual time so delay() doesn't actually wait.
        // This test verifies that the provider accepts delay configuration
        // and completes without error.
        val provider = FakeAiProvider(mode = FakeMode.DEMO, delayMs = 1000)

        val result = provider.chat("system", emptyList(), emptyList())

        assertNotNull(result)
    }

    // ── Response ID format test ───────────────────────────────────────────────

    @Test
    fun `response IDs are unique and prefixed with fake-`() = runTest {
        val provider = FakeAiProvider(mode = FakeMode.DEMO)

        val response1 = provider.chat("system", listOf(AiMessage("user", "1")), emptyList())
        val response2 = provider.chat("system", listOf(AiMessage("user", "2")), emptyList())

        assertTrue(response1.id.startsWith("fake-"))
        assertTrue(response2.id.startsWith("fake-"))
        assertTrue(response1.id != response2.id, "Response IDs should be unique")
    }
}

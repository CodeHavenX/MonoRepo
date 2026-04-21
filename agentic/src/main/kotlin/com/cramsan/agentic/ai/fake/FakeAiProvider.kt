package com.cramsan.agentic.ai.fake

import com.cramsan.agentic.ai.AiContentBlock
import com.cramsan.agentic.ai.AiMessage
import com.cramsan.agentic.ai.AiProvider
import com.cramsan.agentic.ai.AiResponse
import com.cramsan.agentic.ai.AiTool
import com.cramsan.agentic.core.FakeMode
import com.cramsan.framework.logging.logD
import com.cramsan.framework.logging.logI
import kotlinx.coroutines.delay
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicInteger

private const val TAG = "FakeAiProvider"

/**
 * A fake AI provider for testing and demos.
 *
 * In TEST mode, responses must be explicitly queued via [enqueueResponse] or convenience methods.
 * If the queue is empty, an exception is thrown.
 *
 * In DEMO mode, if no response is queued, a response is generated based on context:
 * - Pattern matching on user messages to select appropriate tools
 * - Auto-completion after [autoCompleteAfterTurns] interactions
 * - Default text response as fallback
 */
class FakeAiProvider(
    private val model: String = "fake-model",
    private val mode: FakeMode = FakeMode.TEST,
    private val delayMs: Long = 0L,
    private val autoCompleteAfterTurns: Int = 5,
    private val defaultTextResponse: String = "I understand. Let me continue working on this task.",
) : AiProvider {

    private val responseQueue: ConcurrentLinkedQueue<AiResponse> = ConcurrentLinkedQueue()
    private val capturedRequests: MutableList<CapturedRequest> = mutableListOf()
    private val nextId = AtomicInteger(1)
    private val interactionCount = AtomicInteger(0)

    /**
     * A captured request for test inspection.
     */
    data class CapturedRequest(
        val systemPrompt: String,
        val messages: List<AiMessage>,
        val tools: List<AiTool>,
    )

    override suspend fun chat(
        systemPrompt: String,
        messages: List<AiMessage>,
        tools: List<AiTool>,
    ): AiResponse {
        logD(TAG, "chat() called: mode=$mode, model=$model, messageCount=${messages.size}, toolCount=${tools.size}")

        // Capture request for test inspection
        synchronized(capturedRequests) {
            capturedRequests.add(CapturedRequest(systemPrompt, messages, tools))
        }
        logD(TAG, "Request captured (total captured: ${capturedRequests.size})")

        // Apply simulated delay
        if (delayMs > 0) {
            logD(TAG, "Simulating delay of ${delayMs}ms")
            delay(delayMs)
        }

        // Check queue first (both modes)
        val queuedResponse = responseQueue.poll()
        if (queuedResponse != null) {
            logI(TAG, "Returning queued response: id=${queuedResponse.id}")
            return queuedResponse
        }

        // In TEST mode, fail if no response queued
        if (mode == FakeMode.TEST) {
            logD(TAG, "TEST mode: no queued response, throwing exception")
            error("FakeAiProvider in TEST mode: no response queued. Use enqueueResponse() to set up expected responses.")
        }

        // DEMO mode: generate response based on context
        logD(TAG, "DEMO mode: generating response based on context")
        return generateDemoResponse(messages, tools)
    }

    // ── Test helper methods ───────────────────────────────────────────────────

    /**
     * Enqueue a response to be returned by the next [chat] call.
     */
    fun enqueueResponse(response: AiResponse) {
        logD(TAG, "Enqueuing response: id=${response.id}, contentBlocks=${response.content.size}")
        responseQueue.add(response)
    }

    /**
     * Enqueue multiple responses in order.
     */
    fun enqueueResponses(vararg responses: AiResponse) {
        responses.forEach { enqueueResponse(it) }
    }

    /**
     * Convenience: enqueue a simple text response.
     * @return the created response for assertions
     */
    fun enqueueTextResponse(text: String, stopReason: String = "end_turn"): AiResponse {
        val response = AiResponse(
            id = "fake-${nextId.getAndIncrement()}",
            content = listOf(AiContentBlock.Text(text)),
            stopReason = stopReason,
        )
        enqueueResponse(response)
        return response
    }

    /**
     * Convenience: enqueue a tool call response.
     * @return the created response for assertions
     */
    fun enqueueToolCallResponse(
        toolName: String,
        toolInput: JsonObject,
        toolId: String = "tool-${nextId.getAndIncrement()}",
    ): AiResponse {
        val response = AiResponse(
            id = "fake-${nextId.getAndIncrement()}",
            content = listOf(AiContentBlock.ToolCall(toolId, toolName, toolInput)),
            stopReason = "tool_use",
        )
        enqueueResponse(response)
        return response
    }

    /**
     * Get all captured requests for assertion.
     */
    fun getCapturedRequests(): List<CapturedRequest> {
        synchronized(capturedRequests) {
            return capturedRequests.toList()
        }
    }

    /**
     * Get the last captured request.
     */
    fun getLastRequest(): CapturedRequest? {
        synchronized(capturedRequests) {
            return capturedRequests.lastOrNull()
        }
    }

    /**
     * Clear all captured requests.
     */
    fun clearCapturedRequests() {
        synchronized(capturedRequests) {
            logD(TAG, "Clearing ${capturedRequests.size} captured requests")
            capturedRequests.clear()
        }
    }

    /**
     * Clear response queue.
     */
    fun clearQueue() {
        logD(TAG, "Clearing response queue (size=${responseQueue.size})")
        responseQueue.clear()
    }

    /**
     * Reset all state.
     */
    fun reset() {
        logD(TAG, "Resetting FakeAiProvider state")
        clearQueue()
        clearCapturedRequests()
        interactionCount.set(0)
    }

    /**
     * Get number of queued responses remaining.
     */
    fun queueSize(): Int = responseQueue.size

    // ── Demo mode response generation ─────────────────────────────────────────

    private fun generateDemoResponse(
        messages: List<AiMessage>,
        tools: List<AiTool>,
    ): AiResponse {
        val turns = interactionCount.incrementAndGet()
        logD(TAG, "Generating demo response for turn $turns (autoComplete after $autoCompleteAfterTurns)")

        val lastUserMessage = messages.lastOrNull { it.role == "user" }?.content.orEmpty()

        // Check if we should auto-complete
        if (turns >= autoCompleteAfterTurns && tools.any { it.name == "task_complete" }) {
            logI(TAG, "Auto-completing task after $turns turns")
            return createTaskCompleteResponse()
        }

        // Pattern-based tool selection for demo realism
        val toolResponse = selectToolBasedOnContext(lastUserMessage, tools)
        if (toolResponse != null) {
            return toolResponse
        }

        // Default: return text response
        logD(TAG, "Returning default text response")
        return AiResponse(
            id = "fake-${nextId.getAndIncrement()}",
            content = listOf(AiContentBlock.Text(defaultTextResponse)),
            stopReason = "end_turn",
        )
    }

    @Suppress("CyclomaticComplexMethod")
    private fun selectToolBasedOnContext(
        lastMessage: String,
        tools: List<AiTool>,
    ): AiResponse? {
        val messageLower = lastMessage.lowercase()
        val toolNames = tools.map { it.name }.toSet()

        // Look for patterns that suggest tool usage
        val toolSelection: Pair<String, JsonObject>? = when {
            // Starting work - list files first
            (messageLower.contains("begin") || messageLower.contains("start")) &&
                "list_files" in toolNames -> {
                "list_files" to buildJsonObject { put("glob", "**/*.kt") }
            }
            // Read file requests
            messageLower.contains("read") && "read_file" in toolNames -> {
                "read_file" to buildJsonObject { put("path", "README.md") }
            }
            // Write/fix requests
            (messageLower.contains("write") || messageLower.contains("fix") ||
                messageLower.contains("create")) && "write_file" in toolNames -> {
                "write_file" to buildJsonObject {
                    put("path", "output.txt")
                    put("content", "Generated content")
                }
            }
            // Run command requests
            (messageLower.contains("run") || messageLower.contains("execute") ||
                messageLower.contains("command")) && "run_command" in toolNames -> {
                "run_command" to buildJsonObject { put("command", "echo 'Done'") }
            }
            else -> null
        }

        if (toolSelection != null) {
            val (toolName, toolInput) = toolSelection
            logD(TAG, "Pattern matched: selecting tool '$toolName'")
            return AiResponse(
                id = "fake-${nextId.getAndIncrement()}",
                content = listOf(
                    AiContentBlock.ToolCall(
                        id = "tool-${nextId.getAndIncrement()}",
                        name = toolName,
                        input = toolInput,
                    )
                ),
                stopReason = "tool_use",
            )
        }

        return null
    }

    private fun createTaskCompleteResponse(): AiResponse {
        return AiResponse(
            id = "fake-${nextId.getAndIncrement()}",
            content = listOf(
                AiContentBlock.ToolCall(
                    id = "tool-${nextId.getAndIncrement()}",
                    name = "task_complete",
                    input = buildJsonObject {
                        put("prTitle", "Demo Task Complete")
                        put("prBody", "This task was completed in demo mode.")
                    },
                ),
            ),
            stopReason = "tool_use",
        )
    }
}

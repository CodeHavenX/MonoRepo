package com.cramsan.agentic.core

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/**
 * Wire-format models for the Anthropic Messages API. These are internal to
 * [com.cramsan.agentic.ai.claude.ClaudeAiProvider] and should not be used outside the `ai.claude` package.
 * The public AI abstraction uses [com.cramsan.agentic.ai.AiMessage], [com.cramsan.agentic.ai.AiTool],
 * and [com.cramsan.agentic.ai.AiResponse] instead.
 */
@Serializable
data class ClaudeMessage(val role: String, val content: String)

/** Serialized representation of a tool definition sent in the Anthropic API request body. */
@Serializable
data class ClaudeTool(
    val name: String,
    val description: String,
    val inputSchema: JsonObject,
)

/** Top-level response from the Anthropic Messages API (`POST /v1/messages`). */
@Serializable
data class ClaudeResponse(
    val id: String,
    val content: List<ClaudeContentBlock>,
    @SerialName("stop_reason")
    val stopReason: String?,
)

/**
 * A discriminated content block within a [ClaudeResponse]. Claude may return a mix of text
 * and tool-use blocks in a single response turn.
 */
@Serializable
sealed class ClaudeContentBlock {
    /** Plain text produced by the model. */
    @Serializable
    @SerialName("text")
    data class Text(val text: String) : ClaudeContentBlock()

    /** A request from the model to invoke a tool defined in the request's `tools` array. */
    @Serializable
    @SerialName("tool_use")
    data class ToolUse(
        val id: String,
        val name: String,
        val input: JsonObject,
    ) : ClaudeContentBlock()
}

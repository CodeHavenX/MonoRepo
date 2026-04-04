package com.cramsan.agentic.core

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class ClaudeMessage(val role: String, val content: String)

@Serializable
data class ClaudeTool(
    val name: String,
    val description: String,
    val inputSchema: JsonObject,
)

@Serializable
data class ClaudeResponse(
    val id: String,
    val content: List<ClaudeContentBlock>,
    @SerialName("stop_reason")
    val stopReason: String?,
)

@Serializable
sealed class ClaudeContentBlock {
    @Serializable
    @SerialName("text")
    data class Text(val text: String) : ClaudeContentBlock()

    @Serializable
    @SerialName("tool_use")
    data class ToolUse(
        val id: String,
        val name: String,
        val input: JsonObject,
    ) : ClaudeContentBlock()
}

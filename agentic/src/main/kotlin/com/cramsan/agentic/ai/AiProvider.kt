package com.cramsan.agentic.ai

import kotlinx.serialization.json.JsonObject

data class AiMessage(val role: String, val content: String)

data class AiTool(val name: String, val description: String, val inputSchema: JsonObject)

sealed class AiContentBlock {
    data class Text(val text: String) : AiContentBlock()
    data class ToolCall(val id: String, val name: String, val input: JsonObject) : AiContentBlock()
}

data class AiResponse(
    val id: String,
    val content: List<AiContentBlock>,
    val stopReason: String?,
)

class AiProviderException(message: String, val exitCode: Int = -1) : Exception(message)

interface AiProvider {
    suspend fun chat(
        model: String,
        systemPrompt: String,
        messages: List<AiMessage>,
        tools: List<AiTool>,
    ): AiResponse
}

package com.cramsan.agentic.claude

import com.cramsan.agentic.core.ClaudeMessage
import com.cramsan.agentic.core.ClaudeResponse
import com.cramsan.agentic.core.ClaudeTool

interface ClaudeClient {
    suspend fun chat(
        model: String,
        systemPrompt: String,
        messages: List<ClaudeMessage>,
        tools: List<ClaudeTool>,
    ): ClaudeResponse
}

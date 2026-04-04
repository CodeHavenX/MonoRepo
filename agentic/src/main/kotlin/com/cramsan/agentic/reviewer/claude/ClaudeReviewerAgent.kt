package com.cramsan.agentic.reviewer.claude

import com.cramsan.agentic.claude.ClaudeClient
import com.cramsan.agentic.core.AgenticDocument
import com.cramsan.agentic.core.ClaudeContentBlock
import com.cramsan.agentic.core.ClaudeMessage
import com.cramsan.agentic.core.ReviewerDefinition
import com.cramsan.agentic.core.ReviewerFeedback
import com.cramsan.agentic.core.Task
import com.cramsan.agentic.reviewer.ReviewerAgent
import com.cramsan.framework.logging.logI

private const val TAG = "ClaudeReviewerAgent"

class ClaudeReviewerAgent(
    private val claudeClient: ClaudeClient,
    private val model: String,
) : ReviewerAgent {

    override suspend fun reviewDocuments(
        reviewer: ReviewerDefinition,
        documents: List<AgenticDocument>,
    ): ReviewerFeedback {
        val docsContent = documents.joinToString("\n\n---\n\n") { doc ->
            "## Document: ${doc.relativePath}\n\n(Content not loaded — review based on metadata)"
        }
        val userMessage = ClaudeMessage(
            role = "user",
            content = "Please review the following project documents:\n\n$docsContent",
        )

        logI(TAG, "Reviewer '${reviewer.name}' reviewing ${documents.size} documents")

        val response = claudeClient.chat(
            model = model,
            systemPrompt = reviewer.systemPrompt,
            messages = listOf(userMessage),
            tools = emptyList(),
        )

        val text = response.content.filterIsInstance<ClaudeContentBlock.Text>()
            .firstOrNull()?.text ?: "(no feedback)"

        return ReviewerFeedback(reviewerName = reviewer.name, content = text)
    }

    override suspend fun reviewCode(
        reviewer: ReviewerDefinition,
        task: Task,
        diff: String,
    ): ReviewerFeedback {
        val userMessage = ClaudeMessage(
            role = "user",
            content = "Task: ${task.title}\n\nDiff:\n```\n$diff\n```",
        )

        logI(TAG, "Reviewer '${reviewer.name}' reviewing code for task ${task.id}")

        val response = claudeClient.chat(
            model = model,
            systemPrompt = reviewer.systemPrompt,
            messages = listOf(userMessage),
            tools = emptyList(),
        )

        val text = response.content.filterIsInstance<ClaudeContentBlock.Text>()
            .firstOrNull()?.text ?: "(no feedback)"

        return ReviewerFeedback(reviewerName = reviewer.name, content = text)
    }
}

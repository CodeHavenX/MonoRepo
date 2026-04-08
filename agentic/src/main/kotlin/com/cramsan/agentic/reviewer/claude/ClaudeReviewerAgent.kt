package com.cramsan.agentic.reviewer.claude

import com.cramsan.agentic.ai.AiContentBlock
import com.cramsan.agentic.ai.AiMessage
import com.cramsan.agentic.ai.AiProvider
import com.cramsan.agentic.core.AgenticDocument
import com.cramsan.agentic.core.ReviewerDefinition
import com.cramsan.agentic.core.ReviewerFeedback
import com.cramsan.agentic.core.Task
import com.cramsan.agentic.reviewer.ReviewerAgent
import com.cramsan.framework.logging.logD
import com.cramsan.framework.logging.logI
import com.cramsan.framework.logging.logW

private const val TAG = "ClaudeReviewerAgent"

class ClaudeReviewerAgent(
    private val aiProvider: AiProvider,
) : ReviewerAgent {

    override suspend fun reviewDocuments(
        reviewer: ReviewerDefinition,
        documents: List<AgenticDocument>,
    ): ReviewerFeedback {
        logD(TAG, "reviewDocuments called: reviewer='${reviewer.name}', documentCount=${documents.size}")
        documents.forEach { doc ->
            logD(TAG, "Document to review: id=${doc.id}, path=${doc.relativePath}, status=${doc.status}")
        }

        val docsContent = documents.joinToString("\n\n---\n\n") { doc ->
            "## Document: ${doc.relativePath}\n\n(Content not loaded — review based on metadata)"
        }
        val userMessage = AiMessage(
            role = "user",
            content = "Please review the following project documents:\n\n$docsContent",
        )

        logI(TAG, "Reviewer '${reviewer.name}' reviewing ${documents.size} documents")

        val response = aiProvider.chat(
            systemPrompt = reviewer.systemPrompt,
            messages = listOf(userMessage),
            tools = emptyList(),
        )

        val textBlock = response.content.filterIsInstance<AiContentBlock.Text>().firstOrNull()
        if (textBlock == null) {
            logW(TAG, "Reviewer '${reviewer.name}' returned an empty response for document review; using fallback text")
        }
        val text = textBlock?.text ?: "(no feedback)"
        logI(TAG, "Reviewer '${reviewer.name}' document feedback length: ${text.length} chars")

        return ReviewerFeedback(reviewerName = reviewer.name, content = text)
    }

    override suspend fun reviewCode(
        reviewer: ReviewerDefinition,
        task: Task,
        diff: String,
    ): ReviewerFeedback {
        logD(TAG, "reviewCode called: reviewer='${reviewer.name}', taskId=${task.id}, diffLength=${diff.length}")

        val userMessage = AiMessage(
            role = "user",
            content = "Task: ${task.title}\n\nDiff:\n```\n$diff\n```",
        )

        logI(TAG, "Reviewer '${reviewer.name}' reviewing code for task ${task.id}")

        val response = aiProvider.chat(
            systemPrompt = reviewer.systemPrompt,
            messages = listOf(userMessage),
            tools = emptyList(),
        )

        val textBlock = response.content.filterIsInstance<AiContentBlock.Text>().firstOrNull()
        if (textBlock == null) {
            logW(TAG, "Reviewer '${reviewer.name}' returned an empty response for code review on task ${task.id}; using fallback text")
        }
        val text = textBlock?.text ?: "(no feedback)"
        logI(TAG, "Reviewer '${reviewer.name}' code feedback length: ${text.length} chars")

        return ReviewerFeedback(reviewerName = reviewer.name, content = text)
    }
}

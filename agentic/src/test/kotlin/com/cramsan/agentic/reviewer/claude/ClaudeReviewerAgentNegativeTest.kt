package com.cramsan.agentic.reviewer.claude

import com.cramsan.agentic.ai.AiContentBlock
import com.cramsan.agentic.ai.AiProvider
import com.cramsan.agentic.ai.AiProviderException
import com.cramsan.agentic.ai.AiResponse
import com.cramsan.agentic.core.AgenticDocument
import com.cramsan.agentic.core.DocumentStatus
import com.cramsan.agentic.core.DocumentType
import com.cramsan.agentic.core.ReviewerDefinition
import com.cramsan.agentic.core.Task
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.buildJsonObject
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Negative and edge-case tests for ClaudeReviewerAgent: AI provider exceptions,
 * responses with no text blocks, empty inputs, and ToolCall-only responses.
 */
class ClaudeReviewerAgentNegativeTest {

    private val aiProvider = mockk<AiProvider>()
    private val reviewerAgent = ClaudeReviewerAgent(aiProvider)

    private val reviewer = ReviewerDefinition(
        name = "security",
        systemPrompt = "You are a security reviewer",
    )

    private val task = Task(
        id = "task-001",
        title = "My Task",
        description = "Description",
        dependencies = emptyList(),
    )

    private val sampleDoc = AgenticDocument(
        id = "goals-scope",
        typeId = "goals-scope",
        type = DocumentType.GOALS_SCOPE,
        relativePath = "goals-scope.md",
        status = DocumentStatus.VALIDATED,
        lastModifiedEpochMs = 0L,
    )

    @BeforeEach
    fun setup() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
    }

    // ── reviewDocuments: AI provider failures ─────────────────────────────────

    @Test
    fun `reviewDocuments propagates AiProviderException from AI provider`() = runTest {
        coEvery { aiProvider.chat(any(), any(), any()) } throws AiProviderException("API error", exitCode = 1)

        assertFailsWith<AiProviderException> {
            reviewerAgent.reviewDocuments(reviewer, listOf(sampleDoc))
        }
    }

    @Test
    fun `reviewDocuments propagates generic RuntimeException from AI provider`() = runTest {
        coEvery { aiProvider.chat(any(), any(), any()) } throws RuntimeException("Connection refused")

        assertFailsWith<RuntimeException> {
            reviewerAgent.reviewDocuments(reviewer, listOf(sampleDoc))
        }
    }

    // ── reviewDocuments: responses with no text block ─────────────────────────

    @Test
    fun `reviewDocuments returns fallback text when AI response has no content blocks`() = runTest {
        coEvery { aiProvider.chat(any(), any(), any()) } returns AiResponse(
            id = "r1",
            content = emptyList(),
            stopReason = "end_turn",
        )

        val feedback = reviewerAgent.reviewDocuments(reviewer, listOf(sampleDoc))

        assertEquals("(no feedback)", feedback.content)
        assertEquals("security", feedback.reviewerName)
    }

    @Test
    fun `reviewDocuments returns fallback text when AI response contains only ToolCall blocks`() = runTest {
        coEvery { aiProvider.chat(any(), any(), any()) } returns AiResponse(
            id = "r1",
            content = listOf(
                AiContentBlock.ToolCall("call-1", "some_tool", buildJsonObject {}),
            ),
            stopReason = "tool_use",
        )

        val feedback = reviewerAgent.reviewDocuments(reviewer, listOf(sampleDoc))

        assertEquals("(no feedback)", feedback.content)
    }

    // ── reviewDocuments: empty documents list ─────────────────────────────────

    @Test
    fun `reviewDocuments with empty document list still calls AI provider`() = runTest {
        coEvery { aiProvider.chat(any(), any(), any()) } returns AiResponse(
            id = "r1",
            content = listOf(AiContentBlock.Text("Nothing to review")),
            stopReason = "end_turn",
        )

        val feedback = reviewerAgent.reviewDocuments(reviewer, emptyList())

        assertEquals("Nothing to review", feedback.content)
    }

    // ── reviewDocuments: empty reviewer fields ────────────────────────────────

    @Test
    fun `reviewDocuments with empty reviewer name still returns feedback with that empty name`() = runTest {
        val emptyNameReviewer = ReviewerDefinition(name = "", systemPrompt = "Some prompt")
        coEvery { aiProvider.chat(any(), any(), any()) } returns AiResponse(
            id = "r1",
            content = listOf(AiContentBlock.Text("Feedback")),
            stopReason = "end_turn",
        )

        val feedback = reviewerAgent.reviewDocuments(emptyNameReviewer, emptyList())

        assertEquals("", feedback.reviewerName)
    }

    // ── reviewCode: AI provider failures ─────────────────────────────────────

    @Test
    fun `reviewCode propagates AiProviderException from AI provider`() = runTest {
        coEvery { aiProvider.chat(any(), any(), any()) } throws AiProviderException("Quota exceeded", exitCode = 429)

        assertFailsWith<AiProviderException> {
            reviewerAgent.reviewCode(reviewer, task, "diff content")
        }
    }

    @Test
    fun `reviewCode propagates generic RuntimeException from AI provider`() = runTest {
        coEvery { aiProvider.chat(any(), any(), any()) } throws RuntimeException("Timeout")

        assertFailsWith<RuntimeException> {
            reviewerAgent.reviewCode(reviewer, task, "diff content")
        }
    }

    // ── reviewCode: responses with no text block ──────────────────────────────

    @Test
    fun `reviewCode returns fallback text when AI response has no content blocks`() = runTest {
        coEvery { aiProvider.chat(any(), any(), any()) } returns AiResponse(
            id = "r1",
            content = emptyList(),
            stopReason = "end_turn",
        )

        val feedback = reviewerAgent.reviewCode(reviewer, task, "some diff")

        assertEquals("(no feedback)", feedback.content)
        assertEquals("security", feedback.reviewerName)
    }

    @Test
    fun `reviewCode returns fallback text when AI response contains only ToolCall blocks`() = runTest {
        coEvery { aiProvider.chat(any(), any(), any()) } returns AiResponse(
            id = "r1",
            content = listOf(
                AiContentBlock.ToolCall("call-1", "some_tool", buildJsonObject {}),
            ),
            stopReason = "tool_use",
        )

        val feedback = reviewerAgent.reviewCode(reviewer, task, "diff")

        assertEquals("(no feedback)", feedback.content)
    }

    // ── reviewCode: empty diff ────────────────────────────────────────────────

    @Test
    fun `reviewCode with empty diff still calls AI provider and returns feedback`() = runTest {
        coEvery { aiProvider.chat(any(), any(), any()) } returns AiResponse(
            id = "r1",
            content = listOf(AiContentBlock.Text("No changes detected")),
            stopReason = "end_turn",
        )

        val feedback = reviewerAgent.reviewCode(reviewer, task, "")

        assertEquals("No changes detected", feedback.content)
    }
}

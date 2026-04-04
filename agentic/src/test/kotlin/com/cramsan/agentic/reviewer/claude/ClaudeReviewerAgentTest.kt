package com.cramsan.agentic.reviewer.claude

import com.cramsan.agentic.claude.ClaudeClient
import com.cramsan.agentic.core.ClaudeContentBlock
import com.cramsan.agentic.core.ClaudeResponse
import com.cramsan.agentic.core.ReviewerDefinition
import com.cramsan.agentic.core.Task
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ClaudeReviewerAgentTest {

    private val claudeClient = mockk<ClaudeClient>()
    private val reviewerAgent = ClaudeReviewerAgent(claudeClient, "claude-opus-4-6")

    private val reviewerDef = ReviewerDefinition(
        name = "security",
        systemPrompt = "You are a security reviewer",
    )

    private val task = Task(
        id = "task-001",
        title = "My Task",
        description = "Description",
        dependencies = emptyList(),
    )

    @BeforeEach
    fun setup() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
    }

    private fun makeResponse(text: String) = ClaudeResponse(
        id = "resp-1",
        content = listOf(ClaudeContentBlock.Text(text)),
        stopReason = "end_turn",
    )

    @Test
    fun `reviewDocuments calls claude with tools empty list and returns feedback content`() = runTest {
        coEvery { claudeClient.chat(any(), any(), any(), any()) } returns makeResponse("No security issues found")

        val feedback = reviewerAgent.reviewDocuments(reviewerDef, emptyList())

        assertEquals("security", feedback.reviewerName)
        assertEquals("No security issues found", feedback.content)
        coVerify { claudeClient.chat(any(), reviewerDef.systemPrompt, any(), emptyList()) }
    }

    @Test
    fun `reviewCode includes task title and diff in message`() = runTest {
        val diff = "- old line\n+ new line"
        coEvery { claudeClient.chat(any(), any(), any(), any()) } returns makeResponse("LGTM")

        val feedback = reviewerAgent.reviewCode(reviewerDef, task, diff)

        assertEquals("security", feedback.reviewerName)
        assertEquals("LGTM", feedback.content)
        coVerify {
            claudeClient.chat(
                any(),
                reviewerDef.systemPrompt,
                match { messages -> messages.any { it.content.contains("My Task") && it.content.contains(diff) } },
                emptyList(),
            )
        }
    }

    @Test
    fun `reviewCode passes reviewer name through correctly`() = runTest {
        coEvery { claudeClient.chat(any(), any(), any(), any()) } returns makeResponse("All good")
        val anotherReviewer = ReviewerDefinition("performance", "You check performance")

        val feedback = reviewerAgent.reviewCode(anotherReviewer, task, "")

        assertEquals("performance", feedback.reviewerName)
    }
}

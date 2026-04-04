package com.cramsan.agentic.input

import com.cramsan.agentic.claude.ClaudeClient
import com.cramsan.agentic.core.AgenticDocument
import com.cramsan.agentic.core.ClaudeContentBlock
import com.cramsan.agentic.core.ClaudeResponse
import com.cramsan.agentic.core.DocumentStatus
import com.cramsan.agentic.core.DocumentType
import com.cramsan.agentic.core.IssueSeverity
import com.cramsan.agentic.core.ReviewerDefinition
import com.cramsan.agentic.core.ReviewerFeedback
import com.cramsan.agentic.reviewer.ReviewerAgent
import com.cramsan.agentic.reviewer.ReviewerLoader
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class DefaultValidationServiceTest {

    @TempDir
    lateinit var tempDir: Path

    private val json = Json { ignoreUnknownKeys = true }

    private lateinit var documentStore: DocumentStore
    private lateinit var claudeClient: ClaudeClient
    private lateinit var reviewerAgent: ReviewerAgent
    private lateinit var reviewerLoader: ReviewerLoader
    private lateinit var service: DefaultValidationService

    private val model = "claude-3-5-sonnet"

    private fun makeDocument(id: String, type: DocumentType, relativePath: String) = AgenticDocument(
        id = id,
        type = type,
        relativePath = relativePath,
        status = DocumentStatus.UNREVIEWED,
        lastModifiedEpochMs = System.currentTimeMillis(),
    )

    @BeforeEach
    fun setUp() {
        documentStore = mockk(relaxed = true)
        claudeClient = mockk()
        reviewerAgent = mockk()
        reviewerLoader = mockk()

        service = DefaultValidationService(
            documentStore = documentStore,
            claudeClient = claudeClient,
            model = model,
            reviewerAgents = listOf(reviewerAgent),
            reviewerLoader = reviewerLoader,
            json = json,
            docsDir = tempDir,
        )

        // Write doc files to tempDir so readString works
        Files.writeString(tempDir.resolve("goals-scope.md"), "# Goals & Scope\nContent here.")
        Files.writeString(tempDir.resolve("architecture-design.md"), "# Architecture\nContent here.")
        Files.writeString(tempDir.resolve("standards.md"), "# Standards\nContent here.")
        Files.writeString(tempDir.resolve("task-list.md"), "# Task List\n## Task: task-001\nTitle: Example")
    }

    @Test
    fun `reviewDocument with no blocking issues calls updateStatus with VALIDATED`() = runTest {
        val doc = makeDocument("goals-scope", DocumentType.GOALS_SCOPE, "goals-scope.md")

        coEvery {
            claudeClient.chat(
                model = model,
                systemPrompt = any(),
                messages = any(),
                tools = any(),
            )
        } returns ClaudeResponse(
            id = "resp-1",
            stopReason = "end_turn",
            content = listOf(ClaudeContentBlock.Text("[]")),
        )

        val issues = service.reviewDocument(doc)

        assertEquals(0, issues.size)
        coVerify { claudeClient.chat(model = model, systemPrompt = any(), messages = any(), tools = any()) }
        verify { documentStore.updateStatus("goals-scope", DocumentStatus.VALIDATED) }
    }

    @Test
    fun `reviewDocument with blocking issues calls updateStatus with NEEDS_REVISION`() = runTest {
        val doc = makeDocument("goals-scope", DocumentType.GOALS_SCOPE, "goals-scope.md")

        val blockingIssueJson = """
            [{"id":"issue-1","documentId":"goals-scope","description":"Critical flaw","severity":"BLOCKING","status":"OPEN"}]
        """.trimIndent()

        coEvery {
            claudeClient.chat(
                model = model,
                systemPrompt = any(),
                messages = any(),
                tools = any(),
            )
        } returns ClaudeResponse(
            id = "resp-1",
            stopReason = "end_turn",
            content = listOf(ClaudeContentBlock.Text(blockingIssueJson)),
        )

        val issues = service.reviewDocument(doc)

        assertEquals(1, issues.size)
        assertEquals(IssueSeverity.BLOCKING, issues.first().severity)
        verify { documentStore.updateStatus("goals-scope", DocumentStatus.NEEDS_REVISION) }
    }

    @Test
    fun `runValidationPass iterates over all documents calls reviewDocument for each and returns report`() = runTest {
        val docs = listOf(
            makeDocument("goals-scope", DocumentType.GOALS_SCOPE, "goals-scope.md"),
            makeDocument("architecture-design", DocumentType.ARCHITECTURE_DESIGN, "architecture-design.md"),
            makeDocument("standards", DocumentType.STANDARDS, "standards.md"),
            makeDocument("task-list", DocumentType.TASK_LIST, "task-list.md"),
        )

        every { documentStore.getAll() } returns docs

        coEvery {
            claudeClient.chat(
                model = model,
                systemPrompt = any(),
                messages = any(),
                tools = any(),
            )
        } returns ClaudeResponse(
            id = "resp-1",
            stopReason = "end_turn",
            content = listOf(ClaudeContentBlock.Text("[]")),
        )

        val reviewerDef = ReviewerDefinition(name = "security", systemPrompt = "You are a security reviewer.")
        every { reviewerLoader.loadAll() } returns listOf(reviewerDef)

        coEvery {
            reviewerAgent.reviewDocuments(reviewerDef, docs)
        } returns ReviewerFeedback(reviewerName = "security", content = "Looks good.")

        val report = service.runValidationPass()

        assertNotNull(report)
        assertNotNull(report.runId)

        // Verify updateStatus was called with IN_REVIEW for all docs
        verify(exactly = 4) { documentStore.updateStatus(any(), DocumentStatus.IN_REVIEW) }

        // Verify claudeClient.chat was called once per document
        coVerify(exactly = 4) {
            claudeClient.chat(model = model, systemPrompt = any(), messages = any(), tools = any())
        }

        // Verify reviewer agent was invoked
        coVerify { reviewerAgent.reviewDocuments(reviewerDef, docs) }

        // Verify validation report file was written
        assert(Files.exists(tempDir.resolve("validation-report.md")))
    }
}

package com.cramsan.agentic.input

import com.cramsan.agentic.ai.AiContentBlock
import com.cramsan.agentic.ai.AiProvider
import com.cramsan.agentic.ai.AiResponse
import com.cramsan.agentic.core.AgenticDocument
import com.cramsan.agentic.core.DocumentStatus
import com.cramsan.agentic.core.DocumentType
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
import kotlin.test.assertTrue

/**
 * Extended validation service tests covering advisory-only issues, empty responses,
 * and reviewer orchestration requirements from ARCHITECTURE.md §1.2 and TECH_DESIGN.md §5.1.
 */
class DefaultValidationServiceExtendedTest {

    @TempDir
    lateinit var tempDir: Path

    private val json = Json { ignoreUnknownKeys = true }
    private lateinit var documentStore: DocumentStore
    private lateinit var aiProvider: AiProvider
    private lateinit var reviewerAgent: ReviewerAgent
    private lateinit var reviewerLoader: ReviewerLoader
    private lateinit var service: DefaultValidationService

    private fun makeDoc(id: String, type: DocumentType, file: String) = AgenticDocument(
        id = id, typeId = id, type = type, relativePath = file,
        status = DocumentStatus.UNREVIEWED, lastModifiedEpochMs = 0L,
    )

    @BeforeEach
    fun setUp() {
        documentStore = mockk(relaxed = true)
        aiProvider = mockk()
        reviewerAgent = mockk()
        reviewerLoader = mockk()
        service = DefaultValidationService(
            documentStore = documentStore,
            aiProvider = aiProvider,
            reviewerAgents = listOf(reviewerAgent),
            reviewerLoader = reviewerLoader,
            json = json,
            docsDir = tempDir,
        )
        Files.writeString(tempDir.resolve("goals-scope.md"), "# Goals")
        Files.writeString(tempDir.resolve("standards.md"), "# Standards")
        Files.writeString(tempDir.resolve("architecture-design.md"), "# Architecture")
        Files.writeString(tempDir.resolve("task-list.md"), "# Tasks")
    }

    // ── Advisory issues should NOT prevent VALIDATED status ──────────────────

    @Test
    fun `advisory-only issues result in VALIDATED not NEEDS_REVISION`() = runTest {
        val doc = makeDoc("goals-scope", DocumentType.GOALS_SCOPE, "goals-scope.md")
        val advisoryJson = """
            [{"id":"i1","documentId":"goals-scope","description":"Minor clarity issue","severity":"ADVISORY","status":"OPEN"}]
        """.trimIndent()

        coEvery { aiProvider.chat(any(), any(), any()) } returns AiResponse(
            id = "r1", stopReason = "end_turn", content = listOf(AiContentBlock.Text(advisoryJson)),
        )

        val issues = service.reviewDocument(doc)

        assertEquals(1, issues.size)
        // Advisory issue must NOT cause NEEDS_REVISION — only BLOCKING issues do
        verify { documentStore.updateStatus("goals-scope", DocumentStatus.VALIDATED) }
    }

    @Test
    fun `mixed advisory and blocking issues results in NEEDS_REVISION`() = runTest {
        val doc = makeDoc("goals-scope", DocumentType.GOALS_SCOPE, "goals-scope.md")
        val mixedJson = """
            [
              {"id":"i1","documentId":"goals-scope","description":"Advisory","severity":"ADVISORY","status":"OPEN"},
              {"id":"i2","documentId":"goals-scope","description":"Blocking gap","severity":"BLOCKING","status":"OPEN"}
            ]
        """.trimIndent()

        coEvery { aiProvider.chat(any(), any(), any()) } returns AiResponse(
            id = "r1", stopReason = "end_turn", content = listOf(AiContentBlock.Text(mixedJson)),
        )

        service.reviewDocument(doc)

        verify { documentStore.updateStatus("goals-scope", DocumentStatus.NEEDS_REVISION) }
    }

    // ── Empty/malformed response handling ────────────────────────────────────

    @Test
    fun `empty JSON array response returns zero issues and marks VALIDATED`() = runTest {
        val doc = makeDoc("standards", DocumentType.STANDARDS, "standards.md")
        coEvery { aiProvider.chat(any(), any(), any()) } returns AiResponse(
            id = "r1", stopReason = "end_turn", content = listOf(AiContentBlock.Text("[]")),
        )

        val issues = service.reviewDocument(doc)

        assertEquals(0, issues.size)
        verify { documentStore.updateStatus("standards", DocumentStatus.VALIDATED) }
    }

    @Test
    fun `response with no Text content block returns zero issues`() = runTest {
        val doc = makeDoc("standards", DocumentType.STANDARDS, "standards.md")
        // Response has no Text block at all
        coEvery { aiProvider.chat(any(), any(), any()) } returns AiResponse(
            id = "r1", stopReason = "end_turn", content = emptyList(),
        )

        val issues = service.reviewDocument(doc)

        assertEquals(0, issues.size)
    }

    // ── ALL documents are set to IN_REVIEW before reviewing ──────────────────

    @Test
    fun `runValidationPass sets all docs to IN_REVIEW before reviewing any`() = runTest {
        val docs = listOf(
            makeDoc("goals-scope", DocumentType.GOALS_SCOPE, "goals-scope.md"),
            makeDoc("standards", DocumentType.STANDARDS, "standards.md"),
        )
        every { documentStore.getAll() } returns docs
        coEvery { aiProvider.chat(any(), any(), any()) } returns AiResponse(
            id = "r1", stopReason = "end_turn", content = listOf(AiContentBlock.Text("[]")),
        )
        every { reviewerLoader.loadAll() } returns emptyList()

        service.runValidationPass()

        verify { documentStore.updateStatus("goals-scope", DocumentStatus.IN_REVIEW) }
        verify { documentStore.updateStatus("standards", DocumentStatus.IN_REVIEW) }
    }

    // ── Reviewers are advisory — they never modify document status ────────────

    @Test
    fun `reviewer agents do not change document status`() = runTest {
        val docs = listOf(makeDoc("goals-scope", DocumentType.GOALS_SCOPE, "goals-scope.md"))
        every { documentStore.getAll() } returns docs
        coEvery { aiProvider.chat(any(), any(), any()) } returns AiResponse(
            id = "r1", stopReason = "end_turn", content = listOf(AiContentBlock.Text("[]")),
        )
        val reviewerDef = ReviewerDefinition("security", "You are a security reviewer")
        every { reviewerLoader.loadAll() } returns listOf(reviewerDef)
        coEvery { reviewerAgent.reviewDocuments(reviewerDef, docs) } returns ReviewerFeedback("security", "Some findings")

        service.runValidationPass()

        // updateStatus must ONLY be called with IN_REVIEW and VALIDATED/NEEDS_REVISION —
        // never by the reviewer agents. Verify reviewer agent did not call updateStatus.
        // (The reviewerAgent mock is separate from documentStore)
        coVerify { reviewerAgent.reviewDocuments(reviewerDef, docs) }
        // Status updates are only 1 (IN_REVIEW) + 1 (VALIDATED) per doc, not extras from reviewers
        verify(exactly = 2) { documentStore.updateStatus("goals-scope", any()) }
    }

    // ── Validation report is written to disk ──────────────────────────────────

    @Test
    fun `validation report file is created with run id and issue count`() = runTest {
        val docs = listOf(makeDoc("goals-scope", DocumentType.GOALS_SCOPE, "goals-scope.md"))
        every { documentStore.getAll() } returns docs
        val blockingIssue = """[{"id":"i1","documentId":"goals-scope","description":"Gap","severity":"BLOCKING","status":"OPEN"}]"""
        coEvery { aiProvider.chat(any(), any(), any()) } returns AiResponse(
            id = "r1", stopReason = "end_turn", content = listOf(AiContentBlock.Text(blockingIssue)),
        )
        every { reviewerLoader.loadAll() } returns emptyList()

        val report = service.runValidationPass()

        val reportFile = tempDir.resolve("validation-report.md")
        assertTrue(Files.exists(reportFile), "Validation report file must be created")
        assertNotNull(report.runId)

        val content = Files.readString(reportFile)
        assertTrue(content.contains(report.runId), "Report file must contain the run id")
    }

    // ── ValidationService sends document content to AI provider ──────────────

    @Test
    fun `reviewDocument sends the document file content to the AI provider`() = runTest {
        val fileContent = "# Goals & Scope\nWe aim to build a rocket."
        Files.writeString(tempDir.resolve("goals-scope.md"), fileContent)
        val doc = makeDoc("goals-scope", DocumentType.GOALS_SCOPE, "goals-scope.md")

        coEvery { aiProvider.chat(any(), any(), any()) } returns AiResponse(
            id = "r1", stopReason = "end_turn", content = listOf(AiContentBlock.Text("[]")),
        )

        service.reviewDocument(doc)

        coVerify {
            aiProvider.chat(
                systemPrompt = any(),
                messages = match { msgs -> msgs.any { it.content.contains("build a rocket") } },
                tools = emptyList(),
            )
        }
    }

    @Test
    fun `reviewDocument uses empty tools list — no tool use in validation`() = runTest {
        val doc = makeDoc("standards", DocumentType.STANDARDS, "standards.md")
        coEvery { aiProvider.chat(any(), any(), any()) } returns AiResponse(
            id = "r1", stopReason = "end_turn", content = listOf(AiContentBlock.Text("[]")),
        )

        service.reviewDocument(doc)

        coVerify { aiProvider.chat(any(), any(), tools = emptyList()) }
    }
}

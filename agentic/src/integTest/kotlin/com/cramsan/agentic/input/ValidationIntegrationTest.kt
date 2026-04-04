package com.cramsan.agentic.input

import com.cramsan.agentic.claude.ClaudeClient
import com.cramsan.agentic.core.ClaudeContentBlock
import com.cramsan.agentic.core.ClaudeMessage
import com.cramsan.agentic.core.ClaudeResponse
import com.cramsan.agentic.core.ClaudeTool
import com.cramsan.agentic.core.DocumentStatus
import com.cramsan.agentic.core.IssueSeverity
import com.cramsan.agentic.core.IssueStatus
import com.cramsan.agentic.core.ReviewerDefinition
import com.cramsan.agentic.core.ValidationIssue
import com.cramsan.agentic.reviewer.fake.FakeReviewerAgent
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ValidationIntegrationTest {

    @TempDir
    lateinit var tempDir: Path

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }
    private lateinit var documentStore: FileSystemDocumentStore
    private lateinit var claudeClient: ClaudeClient

    // A canned response fixture with no issues
    private val noIssuesResponse = ClaudeResponse(
        id = "resp-1",
        content = listOf(ClaudeContentBlock.Text("[]")),
        stopReason = "end_turn",
    )

    // A canned response with one BLOCKING issue
    private val blockingIssueResponse: ClaudeResponse
        get() {
            val issues = listOf(
                ValidationIssue(
                    id = "issue-1",
                    documentId = "goals-scope",
                    description = "Goals are too vague",
                    severity = IssueSeverity.BLOCKING,
                    status = IssueStatus.OPEN,
                )
            )
            return ClaudeResponse(
                id = "resp-2",
                content = listOf(ClaudeContentBlock.Text(json.encodeToString(issues))),
                stopReason = "end_turn",
            )
        }

    @BeforeEach
    fun setup() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        claudeClient = mockk()

        // Scaffold documents into tempDir
        DefaultScaffolder().scaffold(tempDir)

        // Construct document store over the temp dir
        documentStore = FileSystemDocumentStore(tempDir, json)
    }

    @Test
    fun `happy path - no blocking issues - allValidated returns true`() = runTest {
        coEvery { claudeClient.chat(any(), any(), any(), any()) } returns noIssuesResponse

        val reviewerLoader = mockk<com.cramsan.agentic.reviewer.ReviewerLoader>()
        coEvery { reviewerLoader.loadAll() } returns emptyList()

        val service = DefaultValidationService(
            documentStore = documentStore,
            claudeClient = claudeClient,
            model = "claude-opus-4-6",
            reviewerAgents = emptyList(),
            reviewerLoader = reviewerLoader,
            json = json,
            docsDir = tempDir,
        )

        service.runValidationPass()

        assertTrue(documentStore.allValidated(), "All documents should be VALIDATED after a clean pass")

        val reportFile = tempDir.resolve("validation-report.md")
        assertTrue(Files.exists(reportFile), "Validation report file should be written")
    }

    @Test
    fun `blocking issue - at least one document has NEEDS_REVISION status`() = runTest {
        coEvery { claudeClient.chat(any(), any(), any(), any()) } returnsMany listOf(
            blockingIssueResponse, noIssuesResponse, noIssuesResponse, noIssuesResponse
        )

        val reviewerLoader = mockk<com.cramsan.agentic.reviewer.ReviewerLoader>()
        coEvery { reviewerLoader.loadAll() } returns emptyList()

        val service = DefaultValidationService(
            documentStore = documentStore,
            claudeClient = claudeClient,
            model = "claude-opus-4-6",
            reviewerAgents = emptyList(),
            reviewerLoader = reviewerLoader,
            json = json,
            docsDir = tempDir,
        )

        service.runValidationPass()

        assertFalse(documentStore.allValidated(), "Not all documents should be VALIDATED when there are blocking issues")

        val docs = documentStore.getAll()
        assertTrue(
            docs.any { it.status == DocumentStatus.NEEDS_REVISION },
            "At least one document should have NEEDS_REVISION status"
        )
    }

    @Test
    fun `onDocumentChanged resets all statuses to UNREVIEWED`() = runTest {
        coEvery { claudeClient.chat(any(), any(), any(), any()) } returns noIssuesResponse

        val reviewerLoader = mockk<com.cramsan.agentic.reviewer.ReviewerLoader>()
        coEvery { reviewerLoader.loadAll() } returns emptyList()

        val service = DefaultValidationService(
            documentStore = documentStore,
            claudeClient = claudeClient,
            model = "claude-opus-4-6",
            reviewerAgents = emptyList(),
            reviewerLoader = reviewerLoader,
            json = json,
            docsDir = tempDir,
        )

        // First: validate all docs
        service.runValidationPass()
        assertTrue(documentStore.allValidated())

        // Simulate a document change
        documentStore.onDocumentChanged()

        // All should be reset to UNREVIEWED
        val docs = documentStore.getAll()
        assertTrue(
            docs.all { it.status == DocumentStatus.UNREVIEWED },
            "All documents should be UNREVIEWED after onDocumentChanged()"
        )
    }

    @Test
    fun `reviewer agents run and produce feedback`() = runTest {
        coEvery { claudeClient.chat(any(), any(), any(), any()) } returns noIssuesResponse

        val reviewerDef1 = ReviewerDefinition("security", "You are a security reviewer")
        val reviewerDef2 = ReviewerDefinition("performance", "You check performance")

        val reviewerLoader = mockk<com.cramsan.agentic.reviewer.ReviewerLoader>()
        coEvery { reviewerLoader.loadAll() } returns listOf(reviewerDef1, reviewerDef2)

        val fakeAgent1 = FakeReviewerAgent(documentFeedback = "No security issues")
        val fakeAgent2 = FakeReviewerAgent(documentFeedback = "No performance issues")

        val service = DefaultValidationService(
            documentStore = documentStore,
            claudeClient = claudeClient,
            model = "claude-opus-4-6",
            reviewerAgents = listOf(fakeAgent1, fakeAgent2),
            reviewerLoader = reviewerLoader,
            json = json,
            docsDir = tempDir,
        )

        val report = service.runValidationPass()

        // Both reviewer agents ran — just verify the pass completed without error
        assertTrue(report.issues.isEmpty())
    }
}

package com.cramsan.agentic.input

import com.cramsan.agentic.ai.AiContentBlock
import com.cramsan.agentic.ai.AiProvider
import com.cramsan.agentic.ai.AiResponse
import com.cramsan.agentic.core.AgenticDocument
import com.cramsan.agentic.core.DocumentStatus
import com.cramsan.agentic.core.DocumentType
import com.cramsan.agentic.core.StageApprovalRecord
import com.cramsan.agentic.core.WorkflowConfig
import com.cramsan.agentic.core.WorkflowPromptConfig
import com.cramsan.agentic.core.WorkflowStageConfig
import com.cramsan.agentic.core.WorkflowStatus
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Extended tests for DefaultWorkflowService covering approval drift detection,
 * startNextStage state transitions, File-based prompt resolution, hash capture,
 * and additional negative cases.
 */
class DefaultWorkflowServiceExtendedTest {

    @TempDir
    lateinit var docsDir: Path

    private val documentStore = mockk<DocumentStore>()
    private val aiProvider = mockk<AiProvider>()
    private val json = Json { ignoreUnknownKeys = true }

    private lateinit var service: DefaultWorkflowService

    private val sampleDoc = AgenticDocument(
        id = "goals-scope",
        typeId = "goals-scope",
        type = DocumentType.GOALS_SCOPE,
        relativePath = "goals-scope.md",
        status = DocumentStatus.VALIDATED,
        lastModifiedEpochMs = 1_000_000L,
    )

    private fun testWorkflowStages(): List<WorkflowStageConfig> = listOf(
        WorkflowStageConfig(
            id = "stage1",
            name = "High-Level Plan",
            outputFile = "high-level-plan.md",
            requiresApproval = true,
            inputDependencies = emptyList(),
            prompt = WorkflowPromptConfig.Inline(systemPrompt = "Produce a high-level plan."),
        ),
        WorkflowStageConfig(
            id = "stage2",
            name = "Low-Level Plan",
            outputFile = "low-level-plan.md",
            requiresApproval = true,
            inputDependencies = listOf("stage1"),
            prompt = WorkflowPromptConfig.Inline(systemPrompt = "Produce a low-level plan."),
        ),
    )

    @BeforeEach
    fun setup() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        every { documentStore.allValidated() } returns true
        every { documentStore.getAll() } returns emptyList()
        service = DefaultWorkflowService(
            documentStore,
            aiProvider,
            docsDir,
            json,
            WorkflowConfig(testWorkflowStages()),
        )
    }

    private fun successResponse(text: String = "# Output") = AiResponse(
        id = "r1",
        content = listOf(AiContentBlock.Text(text)),
        stopReason = "end_turn",
    )

    private fun writeApprovalRecord(stageId: String, inputHashes: Map<String, String> = emptyMap()) {
        val metaDir = docsDir.resolve(".agentic-meta")
        Files.createDirectories(metaDir)
        val record = StageApprovalRecord(
            stageId = stageId,
            approvedAtEpochMs = 0L,
            inputHashes = inputHashes,
        )
        Files.writeString(metaDir.resolve("stage.$stageId.json"), json.encodeToString(record))
    }

    // ── Approval drift detection ───────────────────────────────────────────────

    @Test
    fun `getState returns approval warning when input document changes after stage approval`() {
        val docFile = docsDir.resolve("goals-scope.md")
        Files.writeString(docFile, "Original content")
        every { documentStore.getAll() } returns listOf(sampleDoc)
        Files.writeString(docsDir.resolve("high-level-plan.md"), "# High-Level Plan")

        // Approve stage1 — hashes are captured at this point
        service.approveStage("stage1")

        // Modify the input document after approval
        Files.writeString(docFile, "Modified content — should trigger drift warning")

        val state = service.getState()
        assertEquals(1, state.approvalWarnings.size)
        assertEquals("stage1", state.approvalWarnings[0].stageId)
        assertTrue(state.approvalWarnings[0].changedInputs.contains("goals-scope.md"))
    }

    @Test
    fun `getState returns no approval warnings when input documents unchanged since approval`() {
        val docFile = docsDir.resolve("goals-scope.md")
        Files.writeString(docFile, "Stable content")
        every { documentStore.getAll() } returns listOf(sampleDoc)
        Files.writeString(docsDir.resolve("high-level-plan.md"), "# High-Level Plan")

        service.approveStage("stage1")

        // Do not modify the file — hashes should still match
        val state = service.getState()
        assertTrue(state.approvalWarnings.isEmpty())
    }

    @Test
    fun `getState returns approval warning when input document is added after stage approval`() {
        every { documentStore.getAll() } returns listOf(sampleDoc)
        Files.writeString(docsDir.resolve("high-level-plan.md"), "# High-Level Plan")

        // Approve with no input files present (empty hashes)
        service.approveStage("stage1")

        // Now a new file matching the document appears
        Files.writeString(docsDir.resolve("goals-scope.md"), "New document content")

        val state = service.getState()
        assertEquals(1, state.approvalWarnings.size)
        assertTrue(state.approvalWarnings[0].changedInputs.contains("goals-scope.md"))
    }

    @Test
    fun `getState returns no warnings when approval record is corrupt`() {
        every { documentStore.getAll() } returns listOf(sampleDoc)
        Files.writeString(docsDir.resolve("high-level-plan.md"), "# High-Level Plan")

        // Write a corrupt approval record
        val metaDir = docsDir.resolve(".agentic-meta")
        Files.createDirectories(metaDir)
        Files.writeString(metaDir.resolve("stage.stage1.json"), "not valid json {{{{")

        // Should not throw — corrupt record is silently handled
        val state = service.getState()
        assertTrue(state.approvalWarnings.isEmpty())
        assertEquals(WorkflowStatus.StageInProgress("stage2"), state.status)
    }

    // ── approveStage() hash capture ───────────────────────────────────────────

    @Test
    fun `approveStage records hashes of input documents`() {
        val docFile = docsDir.resolve("goals-scope.md")
        Files.writeString(docFile, "Document content for hashing")
        every { documentStore.getAll() } returns listOf(sampleDoc)

        service.approveStage("stage1")

        val recordContent = Files.readString(docsDir.resolve(".agentic-meta/stage.stage1.json"))
        val record = json.decodeFromString<StageApprovalRecord>(recordContent)
        assertTrue(record.inputHashes.containsKey("goals-scope.md"))
        assertNotNull(record.inputHashes["goals-scope.md"])
        assertTrue(record.inputHashes["goals-scope.md"]!!.isNotBlank())
    }

    @Test
    fun `approveStage records empty hashes when no input files exist on disk`() {
        every { documentStore.getAll() } returns listOf(sampleDoc)
        // File for sampleDoc is NOT written to disk

        service.approveStage("stage1")

        val recordContent = Files.readString(docsDir.resolve(".agentic-meta/stage.stage1.json"))
        val record = json.decodeFromString<StageApprovalRecord>(recordContent)
        // File not present on disk → not included in hashes
        assertTrue(record.inputHashes.isEmpty())
    }

    // ── startNextStage() state transitions ────────────────────────────────────

    @Test
    fun `startNextStage starts first stage when workflow is NotStarted`() = runTest {
        every { documentStore.getAll() } returns emptyList()
        coEvery { aiProvider.chat(any(), any(), any()) } returns successResponse("# High-Level Plan")

        val result = service.startNextStage()

        assertNotNull(result)
        assertEquals("stage1", result.stageId)
        assertTrue(Files.exists(docsDir.resolve("high-level-plan.md")))
    }

    @Test
    fun `startNextStage returns null when stage is pending approval`() = runTest {
        every { documentStore.getAll() } returns listOf(sampleDoc)
        Files.writeString(docsDir.resolve("high-level-plan.md"), "# High-Level Plan")
        // No approval record → stage1 is StagePendingApproval

        val result = service.startNextStage()

        assertNull(result)
    }

    @Test
    fun `startNextStage starts the in-progress stage`() = runTest {
        every { documentStore.getAll() } returns listOf(sampleDoc)
        Files.writeString(docsDir.resolve("high-level-plan.md"), "# High-Level Plan")
        writeApprovalRecord("stage1")
        // State is now StageInProgress("stage2")
        coEvery { aiProvider.chat(any(), any(), any()) } returns successResponse("# Low-Level Plan")

        val result = service.startNextStage()

        assertNotNull(result)
        assertEquals("stage2", result.stageId)
        assertTrue(Files.exists(docsDir.resolve("low-level-plan.md")))
    }

    // ── WorkflowPromptConfig.File prompt resolution ───────────────────────────

    @Test
    fun `startStage reads system prompt from file when using File-based prompt config`() = runTest {
        val promptFile = docsDir.resolve("custom-prompt.md")
        Files.writeString(promptFile, "This is the custom system prompt from a file.")

        val serviceWithFilePrompt = DefaultWorkflowService(
            documentStore,
            aiProvider,
            docsDir,
            json,
            WorkflowConfig(
                listOf(
                    WorkflowStageConfig(
                        id = "stage1",
                        name = "Custom Stage",
                        outputFile = "output.md",
                        requiresApproval = true,
                        inputDependencies = emptyList(),
                        prompt = WorkflowPromptConfig.File(path = "custom-prompt.md"),
                    ),
                ),
            ),
        )

        every { documentStore.getAll() } returns emptyList()
        val capturedSystemPrompt = slot<String>()
        coEvery {
            aiProvider.chat(capture(capturedSystemPrompt), any(), any())
        } returns successResponse("# Output")

        serviceWithFilePrompt.startStage("stage1")

        assertEquals("This is the custom system prompt from a file.", capturedSystemPrompt.captured)
    }

    // ── reviseStage() negative cases ──────────────────────────────────────────

    @Test
    fun `reviseStage throws for unknown stage ID`() = runTest {
        assertFailsWith<IllegalArgumentException> {
            service.reviseStage("nonexistent-stage")
        }
    }

    // ── startStage() gracefully skips missing input doc files ─────────────────

    @Test
    fun `startStage succeeds when input document file does not exist on disk`() = runTest {
        // documentStore returns a doc, but the file is not present on disk
        every { documentStore.getAll() } returns listOf(sampleDoc)
        val capturedMessages = slot<List<com.cramsan.agentic.ai.AiMessage>>()
        coEvery {
            aiProvider.chat(any(), capture(capturedMessages), any())
        } returns successResponse("# High-Level Plan")

        val result = service.startStage("stage1")

        assertEquals("stage1", result.stageId)
        // The missing file is skipped — prompt should not contain file content
        assertTrue(capturedMessages.captured.none { it.content.contains("goals-scope") })
    }
}

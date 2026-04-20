package com.cramsan.agentic.input

import com.cramsan.agentic.ai.AiContentBlock
import com.cramsan.agentic.ai.AiProvider
import com.cramsan.agentic.ai.AiResponse
import com.cramsan.agentic.core.AgenticDocument
import com.cramsan.agentic.core.DocumentStatus
import com.cramsan.agentic.core.DocumentType
import com.cramsan.agentic.core.StageApprovalRecord
import com.cramsan.agentic.core.WorkflowConfig
import com.cramsan.agentic.core.WorkflowConfigErrorType
import com.cramsan.agentic.core.WorkflowPromptConfig
import com.cramsan.agentic.core.WorkflowStageConfig
import com.cramsan.agentic.core.WorkflowStatus
import kotlinx.serialization.json.Json
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Tests for DefaultWorkflowService covering getState() derivation,
 * startStage/reviseStage/approveStage methods, validation, and negative cases.
 */
class DefaultWorkflowServiceTest {

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

    /**
     * Test workflow stages using inline prompts (not file-based) to avoid
     * needing resources in the test environment.
     */
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
        WorkflowStageConfig(
            id = "stage3",
            name = "Task List",
            outputFile = "task-list.md",
            requiresApproval = true,
            inputDependencies = listOf("stage2"),
            prompt = WorkflowPromptConfig.Inline(systemPrompt = "Produce a task list."),
        ),
    )

    @BeforeEach
    fun setup() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        every { documentStore.allValidated() } returns true
        service = DefaultWorkflowService(
            documentStore,
            aiProvider,
            docsDir,
            json,
            WorkflowConfig(testWorkflowStages()),
        )
    }

    private fun writeApprovalRecord(stageId: String) {
        val metaDir = docsDir.resolve(".agentic-meta")
        Files.createDirectories(metaDir)
        val record = StageApprovalRecord(
            stageId = stageId,
            approvedAtEpochMs = 0L,
            inputHashes = emptyMap(),
        )
        Files.writeString(metaDir.resolve("stage.$stageId.json"), json.encodeToString(record))
    }

    // ── getState() derivation ───────────────────────────────────────────────────

    @Test
    fun `getState is NotStarted when document store is empty`() {
        every { documentStore.getAll() } returns emptyList()

        val state = service.getState()
        assertEquals(WorkflowStatus.NotStarted, state.status)
        assertTrue(state.completedStages.isEmpty())
        assertNull(state.currentStage)
    }

    @Test
    fun `getState is StageInProgress for stage1 when docs exist but no plan files exist`() {
        every { documentStore.getAll() } returns listOf(sampleDoc)
        // No files written to docsDir

        val state = service.getState()
        assertEquals(WorkflowStatus.StageInProgress("stage1"), state.status)
        assertEquals("stage1", state.currentStage?.id)
    }

    @Test
    fun `getState is StagePendingApproval for stage1 when high-level-plan exists but not approved`() {
        every { documentStore.getAll() } returns listOf(sampleDoc)

        Files.writeString(docsDir.resolve("high-level-plan.md"), "# High-Level Plan")

        val state = service.getState()
        assertEquals(WorkflowStatus.StagePendingApproval("stage1"), state.status)
        assertEquals("stage1", state.currentStage?.id)
    }

    @Test
    fun `getState is StageInProgress for stage2 when high-level-plan is approved`() {
        every { documentStore.getAll() } returns listOf(sampleDoc)

        Files.writeString(docsDir.resolve("high-level-plan.md"), "# High-Level Plan")
        writeApprovalRecord("stage1")

        val state = service.getState()
        assertEquals(WorkflowStatus.StageInProgress("stage2"), state.status)
        assertEquals(listOf("stage1"), state.completedStages)
    }

    @Test
    fun `getState is StagePendingApproval for stage2 when low-level-plan exists but not approved`() {
        every { documentStore.getAll() } returns listOf(sampleDoc)

        Files.writeString(docsDir.resolve("high-level-plan.md"), "# High-Level Plan")
        writeApprovalRecord("stage1")
        Files.writeString(docsDir.resolve("low-level-plan.md"), "# Low-Level Plan")

        val state = service.getState()
        assertEquals(WorkflowStatus.StagePendingApproval("stage2"), state.status)
    }

    @Test
    fun `getState is StageInProgress for stage3 when low-level-plan is approved`() {
        every { documentStore.getAll() } returns listOf(sampleDoc)

        Files.writeString(docsDir.resolve("high-level-plan.md"), "# High-Level Plan")
        writeApprovalRecord("stage1")
        Files.writeString(docsDir.resolve("low-level-plan.md"), "# Low-Level Plan")
        writeApprovalRecord("stage2")

        val state = service.getState()
        assertEquals(WorkflowStatus.StageInProgress("stage3"), state.status)
        assertEquals(listOf("stage1", "stage2"), state.completedStages)
    }

    @Test
    fun `getState is StagePendingApproval for stage3 when task-list exists but not approved`() {
        every { documentStore.getAll() } returns listOf(sampleDoc)

        Files.writeString(docsDir.resolve("high-level-plan.md"), "# High-Level Plan")
        writeApprovalRecord("stage1")
        Files.writeString(docsDir.resolve("low-level-plan.md"), "# Low-Level Plan")
        writeApprovalRecord("stage2")
        Files.writeString(docsDir.resolve("task-list.md"), "# Task List")

        val state = service.getState()
        assertEquals(WorkflowStatus.StagePendingApproval("stage3"), state.status)
    }

    @Test
    fun `getState is Complete when task-list is approved`() {
        every { documentStore.getAll() } returns listOf(sampleDoc)

        Files.writeString(docsDir.resolve("high-level-plan.md"), "# High-Level Plan")
        writeApprovalRecord("stage1")
        Files.writeString(docsDir.resolve("low-level-plan.md"), "# Low-Level Plan")
        writeApprovalRecord("stage2")
        Files.writeString(docsDir.resolve("task-list.md"), "# Task List")
        writeApprovalRecord("stage3")

        val state = service.getState()
        assertEquals(WorkflowStatus.Complete, state.status)
        assertEquals(listOf("stage1", "stage2", "stage3"), state.completedStages)
    }

    // ── startStage() ───────────────────────────────────────────────────────────

    @Test
    fun `startStage writes AI response to output file`() = runTest {
        every { documentStore.getAll() } returns emptyList()
        coEvery { aiProvider.chat(any(), any(), any()) } returns AiResponse(
            id = "r1",
            content = listOf(AiContentBlock.Text("# High-Level Plan\nStuff")),
            stopReason = "end_turn",
        )

        val doc = service.startStage("stage1")

        assertEquals("stage1", doc.stageId)
        assertEquals("High-Level Plan", doc.stageName)
        val written = Files.readString(docsDir.resolve("high-level-plan.md"))
        assertEquals("# High-Level Plan\nStuff", written)
    }

    @Test
    fun `startStage includes input document content in AI prompt`() = runTest {
        Files.writeString(docsDir.resolve("goals-scope.md"), "Project goals go here")
        every { documentStore.getAll() } returns listOf(sampleDoc)
        val capturedMessages = slot<List<com.cramsan.agentic.ai.AiMessage>>()
        coEvery { aiProvider.chat(any(), capture(capturedMessages), any()) } returns AiResponse(
            id = "r1",
            content = listOf(AiContentBlock.Text("# High-Level Plan")),
            stopReason = "end_turn",
        )

        service.startStage("stage1")

        assertTrue(capturedMessages.captured.any { it.content.contains("Project goals go here") })
    }

    @Test
    fun `startStage throws when AI returns no text content`() = runTest {
        every { documentStore.getAll() } returns emptyList()
        coEvery { aiProvider.chat(any(), any(), any()) } returns AiResponse(
            id = "r1",
            content = emptyList(),
            stopReason = "end_turn",
        )

        assertFailsWith<IllegalStateException> {
            service.startStage("stage1")
        }
    }

    @Test
    fun `startStage throws for unknown stage ID`() = runTest {
        assertFailsWith<IllegalArgumentException> {
            service.startStage("unknown-stage")
        }
    }

    @Test
    fun `startStage throws when dependency is not approved`() = runTest {
        // stage2 depends on stage1, but stage1 is not approved
        assertFailsWith<IllegalArgumentException> {
            service.startStage("stage2")
        }
    }

    @Test
    fun `startStage includes dependency output in AI prompt`() = runTest {
        Files.writeString(docsDir.resolve("high-level-plan.md"), "# High-Level Plan\nApproved content")
        writeApprovalRecord("stage1")
        every { documentStore.getAll() } returns emptyList()
        val capturedMessages = slot<List<com.cramsan.agentic.ai.AiMessage>>()
        coEvery { aiProvider.chat(any(), capture(capturedMessages), any()) } returns AiResponse(
            id = "r1",
            content = listOf(AiContentBlock.Text("# Low-Level Plan")),
            stopReason = "end_turn",
        )

        service.startStage("stage2")

        assertTrue(capturedMessages.captured.any { it.content.contains("Approved content") })
    }

    // ── startNextStage() ──────────────────────────────────────────────────────

    @Test
    fun `startNextStage returns null when documents not validated`() = runTest {
        every { documentStore.getAll() } returns listOf(sampleDoc)
        every { documentStore.allValidated() } returns false

        val result = service.startNextStage()
        assertNull(result)
    }

    @Test
    fun `startNextStage returns null when workflow is complete`() = runTest {
        every { documentStore.getAll() } returns listOf(sampleDoc)

        Files.writeString(docsDir.resolve("high-level-plan.md"), "# High-Level Plan")
        writeApprovalRecord("stage1")
        Files.writeString(docsDir.resolve("low-level-plan.md"), "# Low-Level Plan")
        writeApprovalRecord("stage2")
        Files.writeString(docsDir.resolve("task-list.md"), "# Task List")
        writeApprovalRecord("stage3")

        val result = service.startNextStage()
        assertNull(result)
    }

    // ── reviseStage() ─────────────────────────────────────────────────────────

    @Test
    fun `reviseStage throws when output file does not exist`() = runTest {
        assertFailsWith<IllegalArgumentException> {
            service.reviseStage("stage1")
        }
    }

    @Test
    fun `reviseStage overwrites file with AI-revised content`() = runTest {
        Files.writeString(docsDir.resolve("high-level-plan.md"), "# Original with [REVIEWER: fix this]")
        coEvery { aiProvider.chat(any(), any(), any()) } returns AiResponse(
            id = "r1",
            content = listOf(AiContentBlock.Text("# Revised and Clean")),
            stopReason = "end_turn",
        )

        val doc = service.reviseStage("stage1")

        assertEquals("stage1", doc.stageId)
        val written = Files.readString(docsDir.resolve("high-level-plan.md"))
        assertEquals("# Revised and Clean", written)
    }

    @Test
    fun `reviseStage sends annotated file content to AI`() = runTest {
        val annotated = "# High-Level Plan\n[REVIEWER: please expand section 2]"
        Files.writeString(docsDir.resolve("high-level-plan.md"), annotated)
        val capturedMessages = slot<List<com.cramsan.agentic.ai.AiMessage>>()
        coEvery { aiProvider.chat(any(), capture(capturedMessages), any()) } returns AiResponse(
            id = "r1",
            content = listOf(AiContentBlock.Text("# Revised")),
            stopReason = "end_turn",
        )

        service.reviseStage("stage1")

        assertTrue(capturedMessages.captured.any { it.content.contains("please expand section 2") })
    }

    // ── approveStage() ─────────────────────────────────────────────────────────

    @Test
    fun `approveStage creates approval record file`() {
        every { documentStore.getAll() } returns emptyList()

        service.approveStage("stage1")

        assertTrue(Files.exists(docsDir.resolve(".agentic-meta/stage.stage1.json")))
    }

    @Test
    fun `approveStage writes non-empty content to record file`() {
        every { documentStore.getAll() } returns emptyList()

        service.approveStage("stage1")

        val content = Files.readString(docsDir.resolve(".agentic-meta/stage.stage1.json"))
        assertTrue(content.isNotBlank())
    }

    @Test
    fun `approveStage throws for unknown stage ID`() {
        assertFailsWith<IllegalArgumentException> {
            service.approveStage("unknown-stage")
        }
    }

    @Test
    fun `approveStage then getState reflects approval correctly`() {
        every { documentStore.getAll() } returns listOf(sampleDoc)
        Files.writeString(docsDir.resolve("goals-scope.md"), "Project goals go here")
        Files.writeString(docsDir.resolve("high-level-plan.md"), "# High-Level Plan")

        assertEquals(WorkflowStatus.StagePendingApproval("stage1"), service.getState().status)

        service.approveStage("stage1")

        assertEquals(WorkflowStatus.StageInProgress("stage2"), service.getState().status)
    }

    // ── validateWorkflowConfig() ──────────────────────────────────────────────

    @Test
    fun `validateWorkflowConfig returns empty list for valid config`() {
        val errors = service.validateWorkflowConfig()
        assertTrue(errors.isEmpty())
    }

    @Test
    fun `validateWorkflowConfig detects duplicate stage IDs`() {
        val duplicateStages = listOf(
            WorkflowStageConfig(
                id = "stage1",
                name = "First",
                outputFile = "first.md",
                prompt = WorkflowPromptConfig.Inline("Prompt"),
            ),
            WorkflowStageConfig(
                id = "stage1",
                name = "Duplicate",
                outputFile = "duplicate.md",
                prompt = WorkflowPromptConfig.Inline("Prompt"),
            ),
        )
        val serviceWithDuplicates = DefaultWorkflowService(
            documentStore,
            aiProvider,
            docsDir,
            json,
            WorkflowConfig(duplicateStages),
        )

        val errors = serviceWithDuplicates.validateWorkflowConfig()
        assertEquals(1, errors.size)
        assertEquals(WorkflowConfigErrorType.DUPLICATE_STAGE_ID, errors[0].type)
    }

    @Test
    fun `validateWorkflowConfig detects missing dependencies`() {
        val stagesWithMissingDep = listOf(
            WorkflowStageConfig(
                id = "stage1",
                name = "First",
                outputFile = "first.md",
                inputDependencies = listOf("nonexistent"),
                prompt = WorkflowPromptConfig.Inline("Prompt"),
            ),
        )
        val serviceWithMissingDep = DefaultWorkflowService(
            documentStore,
            aiProvider,
            docsDir,
            json,
            WorkflowConfig(stagesWithMissingDep),
        )

        val errors = serviceWithMissingDep.validateWorkflowConfig()
        assertEquals(1, errors.size)
        assertEquals(WorkflowConfigErrorType.MISSING_DEPENDENCY, errors[0].type)
    }

    @Test
    fun `validateWorkflowConfig detects circular dependencies`() {
        val circularStages = listOf(
            WorkflowStageConfig(
                id = "stage1",
                name = "First",
                outputFile = "first.md",
                inputDependencies = listOf("stage2"),
                prompt = WorkflowPromptConfig.Inline("Prompt"),
            ),
            WorkflowStageConfig(
                id = "stage2",
                name = "Second",
                outputFile = "second.md",
                inputDependencies = listOf("stage1"),
                prompt = WorkflowPromptConfig.Inline("Prompt"),
            ),
        )
        val serviceWithCircularDep = DefaultWorkflowService(
            documentStore,
            aiProvider,
            docsDir,
            json,
            WorkflowConfig(circularStages),
        )

        val errors = serviceWithCircularDep.validateWorkflowConfig()
        assertTrue(errors.any { it.type == WorkflowConfigErrorType.CIRCULAR_DEPENDENCY })
    }

    // ── getAllStages() / getStageConfig() ─────────────────────────────────────

    @Test
    fun `getAllStages returns all configured stages`() {
        val stages = service.getAllStages()
        assertEquals(3, stages.size)
        assertEquals(listOf("stage1", "stage2", "stage3"), stages.map { it.id })
    }

    @Test
    fun `getStageConfig returns stage config by ID`() {
        val stage = service.getStageConfig("stage2")
        assertEquals("Low-Level Plan", stage?.name)
    }

    @Test
    fun `getStageConfig returns null for unknown ID`() {
        val stage = service.getStageConfig("nonexistent")
        assertNull(stage)
    }
}

package com.cramsan.agentic.input

import com.cramsan.agentic.ai.AiContentBlock
import com.cramsan.agentic.ai.AiProvider
import com.cramsan.agentic.ai.AiResponse
import com.cramsan.agentic.core.AgenticDocument
import com.cramsan.agentic.core.DocumentStatus
import com.cramsan.agentic.core.DocumentType
import com.cramsan.agentic.core.PlanningStage
import com.cramsan.agentic.core.PlanningStatus
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
import kotlin.test.assertTrue

/**
 * Tests for DefaultPlanningService covering status() derivation in all 9 states,
 * generate/revise/approve methods, and negative cases (missing files, AI failures).
 */
class DefaultPlanningServiceTest {

    @TempDir
    lateinit var docsDir: Path

    private val documentStore = mockk<DocumentStore>()
    private val aiProvider = mockk<AiProvider>()

    private lateinit var service: DefaultPlanningService

    private val sampleDoc = AgenticDocument(
        id = "goals-scope",
        type = DocumentType.GOALS_SCOPE,
        relativePath = "goals-scope.md",
        status = DocumentStatus.VALIDATED,
        lastModifiedEpochMs = 1_000_000L,
    )

    @BeforeEach
    fun setup() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        service = DefaultPlanningService(documentStore, aiProvider, "claude-opus-4-6", docsDir)
    }

    // ── status() derivation ───────────────────────────────────────────────────

    @Test
    fun `status is NOT_STARTED when document store is empty`() {
        every { documentStore.getAll() } returns emptyList()

        assertEquals(PlanningStatus.NOT_STARTED, service.status())
    }

    @Test
    fun `status is AWAITING_DOCUMENT_VALIDATION when not all docs are validated`() {
        every { documentStore.getAll() } returns listOf(sampleDoc)
        every { documentStore.allValidated() } returns false

        assertEquals(PlanningStatus.AWAITING_DOCUMENT_VALIDATION, service.status())
    }

    @Test
    fun `status is STAGE_1_IN_PROGRESS when docs validated but no plan files exist`() {
        every { documentStore.getAll() } returns listOf(sampleDoc)
        every { documentStore.allValidated() } returns true
        // No files written to docsDir

        assertEquals(PlanningStatus.STAGE_1_IN_PROGRESS, service.status())
    }

    @Test
    fun `status is STAGE_1_PENDING_APPROVAL when high-level-plan exists but not approved`() {
        every { documentStore.getAll() } returns listOf(sampleDoc)
        every { documentStore.allValidated() } returns true
        Files.writeString(docsDir.resolve("high-level-plan.md"), "# High-Level Plan")

        assertEquals(PlanningStatus.STAGE_1_PENDING_APPROVAL, service.status())
    }

    @Test
    fun `status is STAGE_2_IN_PROGRESS when high-level-plan is approved`() {
        every { documentStore.getAll() } returns listOf(sampleDoc)
        every { documentStore.allValidated() } returns true
        Files.writeString(docsDir.resolve("high-level-plan.md"), "# High-Level Plan")
        Files.writeString(docsDir.resolve("high-level-plan.approved"), "approved at 0")

        assertEquals(PlanningStatus.STAGE_2_IN_PROGRESS, service.status())
    }

    @Test
    fun `status is STAGE_2_PENDING_APPROVAL when low-level-plan exists but not approved`() {
        every { documentStore.getAll() } returns listOf(sampleDoc)
        every { documentStore.allValidated() } returns true
        Files.writeString(docsDir.resolve("high-level-plan.md"), "# High-Level Plan")
        Files.writeString(docsDir.resolve("high-level-plan.approved"), "approved at 0")
        Files.writeString(docsDir.resolve("low-level-plan.md"), "# Low-Level Plan")

        assertEquals(PlanningStatus.STAGE_2_PENDING_APPROVAL, service.status())
    }

    @Test
    fun `status is STAGE_3_IN_PROGRESS when low-level-plan is approved`() {
        every { documentStore.getAll() } returns listOf(sampleDoc)
        every { documentStore.allValidated() } returns true
        Files.writeString(docsDir.resolve("high-level-plan.md"), "# High-Level Plan")
        Files.writeString(docsDir.resolve("high-level-plan.approved"), "approved at 0")
        Files.writeString(docsDir.resolve("low-level-plan.md"), "# Low-Level Plan")
        Files.writeString(docsDir.resolve("low-level-plan.approved"), "approved at 0")

        assertEquals(PlanningStatus.STAGE_3_IN_PROGRESS, service.status())
    }

    @Test
    fun `status is STAGE_3_PENDING_APPROVAL when task-list exists but not approved`() {
        every { documentStore.getAll() } returns listOf(sampleDoc)
        every { documentStore.allValidated() } returns true
        Files.writeString(docsDir.resolve("high-level-plan.md"), "# High-Level Plan")
        Files.writeString(docsDir.resolve("high-level-plan.approved"), "approved at 0")
        Files.writeString(docsDir.resolve("low-level-plan.md"), "# Low-Level Plan")
        Files.writeString(docsDir.resolve("low-level-plan.approved"), "approved at 0")
        Files.writeString(docsDir.resolve("task-list.md"), "# Task List")

        assertEquals(PlanningStatus.STAGE_3_PENDING_APPROVAL, service.status())
    }

    @Test
    fun `status is COMPLETE when task-list is approved`() {
        every { documentStore.getAll() } returns listOf(sampleDoc)
        every { documentStore.allValidated() } returns true
        Files.writeString(docsDir.resolve("high-level-plan.md"), "# High-Level Plan")
        Files.writeString(docsDir.resolve("high-level-plan.approved"), "approved at 0")
        Files.writeString(docsDir.resolve("low-level-plan.md"), "# Low-Level Plan")
        Files.writeString(docsDir.resolve("low-level-plan.approved"), "approved at 0")
        Files.writeString(docsDir.resolve("task-list.md"), "# Task List")
        Files.writeString(docsDir.resolve("task-list.approved"), "approved at 0")

        assertEquals(PlanningStatus.COMPLETE, service.status())
    }

    // ── generateHighLevelPlan() ───────────────────────────────────────────────

    @Test
    fun `generateHighLevelPlan writes AI response to high-level-plan dot md`() = runTest {
        every { documentStore.getAll() } returns emptyList()
        coEvery { aiProvider.chat(any(), any(), any(), any()) } returns AiResponse(
            id = "r1",
            content = listOf(AiContentBlock.Text("# High-Level Plan\nStuff")),
            stopReason = "end_turn",
        )

        val doc = service.generateHighLevelPlan()

        assertEquals(PlanningStage.HIGH_LEVEL_PLAN, doc.stage)
        val written = Files.readString(docsDir.resolve("high-level-plan.md"))
        assertEquals("# High-Level Plan\nStuff", written)
    }

    @Test
    fun `generateHighLevelPlan includes input document content in AI prompt`() = runTest {
        Files.writeString(docsDir.resolve("goals-scope.md"), "Project goals go here")
        every { documentStore.getAll() } returns listOf(sampleDoc)
        val capturedMessages = slot<List<com.cramsan.agentic.ai.AiMessage>>()
        coEvery { aiProvider.chat(any(), any(), capture(capturedMessages), any()) } returns AiResponse(
            id = "r1",
            content = listOf(AiContentBlock.Text("# High-Level Plan")),
            stopReason = "end_turn",
        )

        service.generateHighLevelPlan()

        assertTrue(capturedMessages.captured.any { it.content.contains("Project goals go here") })
    }

    @Test
    fun `generateHighLevelPlan throws when AI returns no text content`() = runTest {
        every { documentStore.getAll() } returns emptyList()
        coEvery { aiProvider.chat(any(), any(), any(), any()) } returns AiResponse(
            id = "r1",
            content = emptyList(),
            stopReason = "end_turn",
        )

        assertFailsWith<IllegalStateException> {
            service.generateHighLevelPlan()
        }
    }

    @Test
    fun `generateHighLevelPlan propagates AI provider exception`() = runTest {
        every { documentStore.getAll() } returns emptyList()
        coEvery { aiProvider.chat(any(), any(), any(), any()) } throws RuntimeException("API down")

        assertFailsWith<RuntimeException> {
            service.generateHighLevelPlan()
        }
    }

    // ── generateLowLevelPlan() ────────────────────────────────────────────────

    @Test
    fun `generateLowLevelPlan throws when high-level-plan file does not exist`() = runTest {
        every { documentStore.getAll() } returns emptyList()
        // high-level-plan.md not written

        assertFailsWith<Exception> {
            service.generateLowLevelPlan()
        }
    }

    @Test
    fun `generateLowLevelPlan writes AI response to low-level-plan dot md`() = runTest {
        Files.writeString(docsDir.resolve("high-level-plan.md"), "# High-Level Plan")
        every { documentStore.getAll() } returns emptyList()
        coEvery { aiProvider.chat(any(), any(), any(), any()) } returns AiResponse(
            id = "r1",
            content = listOf(AiContentBlock.Text("# Low-Level Plan\nDetails")),
            stopReason = "end_turn",
        )

        val doc = service.generateLowLevelPlan()

        assertEquals(PlanningStage.LOW_LEVEL_PLAN, doc.stage)
        val written = Files.readString(docsDir.resolve("low-level-plan.md"))
        assertEquals("# Low-Level Plan\nDetails", written)
    }

    @Test
    fun `generateLowLevelPlan includes high-level-plan content in AI prompt`() = runTest {
        Files.writeString(docsDir.resolve("high-level-plan.md"), "# High-Level Plan\nApproved content")
        every { documentStore.getAll() } returns emptyList()
        val capturedMessages = slot<List<com.cramsan.agentic.ai.AiMessage>>()
        coEvery { aiProvider.chat(any(), any(), capture(capturedMessages), any()) } returns AiResponse(
            id = "r1",
            content = listOf(AiContentBlock.Text("# Low-Level Plan")),
            stopReason = "end_turn",
        )

        service.generateLowLevelPlan()

        assertTrue(capturedMessages.captured.any { it.content.contains("Approved content") })
    }

    // ── generateTaskList() ────────────────────────────────────────────────────

    @Test
    fun `generateTaskList throws when high-level-plan file does not exist`() = runTest {
        every { documentStore.getAll() } returns emptyList()

        assertFailsWith<Exception> {
            service.generateTaskList()
        }
    }

    @Test
    fun `generateTaskList throws when low-level-plan file does not exist`() = runTest {
        Files.writeString(docsDir.resolve("high-level-plan.md"), "# High-Level Plan")
        every { documentStore.getAll() } returns emptyList()

        assertFailsWith<Exception> {
            service.generateTaskList()
        }
    }

    @Test
    fun `generateTaskList writes AI response to task-list dot md`() = runTest {
        Files.writeString(docsDir.resolve("high-level-plan.md"), "# High-Level Plan")
        Files.writeString(docsDir.resolve("low-level-plan.md"), "# Low-Level Plan")
        every { documentStore.getAll() } returns emptyList()
        coEvery { aiProvider.chat(any(), any(), any(), any()) } returns AiResponse(
            id = "r1",
            content = listOf(AiContentBlock.Text("# Task List\n## Task: TASK-001")),
            stopReason = "end_turn",
        )

        val doc = service.generateTaskList()

        assertEquals(PlanningStage.TASK_LIST, doc.stage)
        val written = Files.readString(docsDir.resolve("task-list.md"))
        assertEquals("# Task List\n## Task: TASK-001", written)
    }

    // ── revise() ─────────────────────────────────────────────────────────────

    @Test
    fun `revise throws when plan file does not exist`() = runTest {
        assertFailsWith<Exception> {
            service.revise(PlanningStage.HIGH_LEVEL_PLAN)
        }
    }

    @Test
    fun `revise overwrites plan file with AI-revised content`() = runTest {
        Files.writeString(docsDir.resolve("high-level-plan.md"), "# Original with [REVIEWER: fix this]")
        coEvery { aiProvider.chat(any(), any(), any(), any()) } returns AiResponse(
            id = "r1",
            content = listOf(AiContentBlock.Text("# Revised and Clean")),
            stopReason = "end_turn",
        )

        val doc = service.revise(PlanningStage.HIGH_LEVEL_PLAN)

        assertEquals(PlanningStage.HIGH_LEVEL_PLAN, doc.stage)
        val written = Files.readString(docsDir.resolve("high-level-plan.md"))
        assertEquals("# Revised and Clean", written)
    }

    @Test
    fun `revise sends annotated file content to AI`() = runTest {
        val annotated = "# High-Level Plan\n[REVIEWER: please expand section 2]"
        Files.writeString(docsDir.resolve("high-level-plan.md"), annotated)
        val capturedMessages = slot<List<com.cramsan.agentic.ai.AiMessage>>()
        coEvery { aiProvider.chat(any(), any(), capture(capturedMessages), any()) } returns AiResponse(
            id = "r1",
            content = listOf(AiContentBlock.Text("# Revised")),
            stopReason = "end_turn",
        )

        service.revise(PlanningStage.HIGH_LEVEL_PLAN)

        assertTrue(capturedMessages.captured.any { it.content.contains("please expand section 2") })
    }

    @Test
    fun `revise throws when AI returns no text content`() = runTest {
        Files.writeString(docsDir.resolve("low-level-plan.md"), "# Low-Level Plan")
        coEvery { aiProvider.chat(any(), any(), any(), any()) } returns AiResponse(
            id = "r1",
            content = emptyList(),
            stopReason = "end_turn",
        )

        assertFailsWith<IllegalStateException> {
            service.revise(PlanningStage.LOW_LEVEL_PLAN)
        }
    }

    // ── approve() ─────────────────────────────────────────────────────────────

    @Test
    fun `approve HIGH_LEVEL_PLAN creates high-level-plan dot approved marker file`() {
        service.approve(PlanningStage.HIGH_LEVEL_PLAN)

        assertTrue(Files.exists(docsDir.resolve("high-level-plan.approved")))
    }

    @Test
    fun `approve LOW_LEVEL_PLAN creates low-level-plan dot approved marker file`() {
        service.approve(PlanningStage.LOW_LEVEL_PLAN)

        assertTrue(Files.exists(docsDir.resolve("low-level-plan.approved")))
    }

    @Test
    fun `approve TASK_LIST creates task-list dot approved marker file`() {
        service.approve(PlanningStage.TASK_LIST)

        assertTrue(Files.exists(docsDir.resolve("task-list.approved")))
    }

    @Test
    fun `approve writes non-empty content to marker file`() {
        service.approve(PlanningStage.HIGH_LEVEL_PLAN)

        val content = Files.readString(docsDir.resolve("high-level-plan.approved"))
        assertTrue(content.isNotBlank())
    }

    @Test
    fun `calling approve twice overwrites the marker file without error`() {
        service.approve(PlanningStage.HIGH_LEVEL_PLAN)
        service.approve(PlanningStage.HIGH_LEVEL_PLAN)

        assertTrue(Files.exists(docsDir.resolve("high-level-plan.approved")))
    }

    @Test
    fun `approve then status reflects approval correctly`() {
        every { documentStore.getAll() } returns listOf(sampleDoc)
        every { documentStore.allValidated() } returns true
        Files.writeString(docsDir.resolve("high-level-plan.md"), "# High-Level Plan")

        assertEquals(PlanningStatus.STAGE_1_PENDING_APPROVAL, service.status())

        service.approve(PlanningStage.HIGH_LEVEL_PLAN)

        assertEquals(PlanningStatus.STAGE_2_IN_PROGRESS, service.status())
    }
}

package com.cramsan.agentic.input

import com.cramsan.agentic.ai.AiContentBlock
import com.cramsan.agentic.ai.AiMessage
import com.cramsan.agentic.ai.AiProvider
import com.cramsan.agentic.core.PlanDocument
import com.cramsan.agentic.core.PlanningStage
import com.cramsan.agentic.core.PlanningStatus
import com.cramsan.framework.logging.logD
import com.cramsan.framework.logging.logI
import java.nio.file.Files
import java.nio.file.Path

private const val TAG = "DefaultPlanningService"

class DefaultPlanningService(
    private val documentStore: DocumentStore,
    private val aiProvider: AiProvider,
    private val model: String,
    private val docsDir: Path,
) : PlanningService {

    private val highLevelPlanFile = docsDir.resolve("high-level-plan.md")
    private val highLevelPlanApproved = docsDir.resolve("high-level-plan.approved")
    private val lowLevelPlanFile = docsDir.resolve("low-level-plan.md")
    private val lowLevelPlanApproved = docsDir.resolve("low-level-plan.approved")
    private val taskListFile = docsDir.resolve("task-list.md")
    private val taskListApproved = docsDir.resolve("task-list.approved")

    override fun status(): PlanningStatus {
        val docs = documentStore.getAll()
        if (docs.isEmpty()) {
            logD(TAG, "status: no documents found → NOT_STARTED")
            return PlanningStatus.NOT_STARTED
        }
        if (!documentStore.allValidated()) {
            logD(TAG, "status: not all documents validated → AWAITING_DOCUMENT_VALIDATION")
            return PlanningStatus.AWAITING_DOCUMENT_VALIDATION
        }
        return when {
            Files.exists(taskListApproved) -> PlanningStatus.COMPLETE
            Files.exists(taskListFile) -> PlanningStatus.STAGE_3_PENDING_APPROVAL
            Files.exists(lowLevelPlanApproved) -> PlanningStatus.STAGE_3_IN_PROGRESS
            Files.exists(lowLevelPlanFile) -> PlanningStatus.STAGE_2_PENDING_APPROVAL
            Files.exists(highLevelPlanApproved) -> PlanningStatus.STAGE_2_IN_PROGRESS
            Files.exists(highLevelPlanFile) -> PlanningStatus.STAGE_1_PENDING_APPROVAL
            else -> PlanningStatus.STAGE_1_IN_PROGRESS
        }.also { logD(TAG, "status: $it") }
    }

    override suspend fun generateHighLevelPlan(): PlanDocument {
        logI(TAG, "Generating high-level plan")
        val inputDocs = readInputDocs()

        val systemPrompt = """
            You are a technical planning assistant. Your task is to produce a high-level plan for
            a software project based on the provided input documents.

            The plan must cover:
            - A summary of the approach to achieving the stated goals
            - Major components or areas of the codebase that will be touched
            - Key technical decisions, constraints, or trade-offs from the architecture and standards documents
            - A rough breakdown of work into logical groups (not yet individual tasks)
            - Any risks or open questions that should be resolved before detailed planning begins

            Write the plan as a clear, human-readable markdown document titled "# High-Level Plan".
            Be concise and focused. Do not include implementation details yet.
        """.trimIndent()

        val content = callAi(systemPrompt, inputDocs)
        Files.writeString(highLevelPlanFile, content)
        logI(TAG, "High-level plan written to $highLevelPlanFile")
        return PlanDocument(PlanningStage.HIGH_LEVEL_PLAN, highLevelPlanFile)
    }

    override suspend fun generateLowLevelPlan(): PlanDocument {
        logI(TAG, "Generating low-level plan")
        val inputDocs = readInputDocs()
        val highLevelPlan = Files.readString(highLevelPlanFile)

        val systemPrompt = """
            You are a technical planning assistant. Your task is to produce a detailed low-level
            plan for a software project based on the provided input documents and an approved
            high-level plan.

            The plan must cover:
            - A detailed breakdown of each logical group from the high-level plan into concrete units of work
            - For each unit: proposed approach, affected modules/files, and dependencies on other units
            - Resolution of any open questions or risks identified in the high-level plan
            - Identification of the critical path through the work

            Write the plan as a clear, human-readable markdown document titled "# Low-Level Plan".
            Be specific and actionable. Each unit of work should be small enough to be completed in a single pull request.
        """.trimIndent()

        val userContent = buildString {
            appendLine(inputDocs)
            appendLine()
            appendLine("---")
            appendLine("## Approved High-Level Plan")
            appendLine()
            appendLine(highLevelPlan)
        }

        val content = callAi(systemPrompt, userContent)
        Files.writeString(lowLevelPlanFile, content)
        logI(TAG, "Low-level plan written to $lowLevelPlanFile")
        return PlanDocument(PlanningStage.LOW_LEVEL_PLAN, lowLevelPlanFile)
    }

    override suspend fun generateTaskList(): PlanDocument {
        logI(TAG, "Generating task list")
        val inputDocs = readInputDocs()
        val highLevelPlan = Files.readString(highLevelPlanFile)
        val lowLevelPlan = Files.readString(lowLevelPlanFile)

        val systemPrompt = """
            You are a technical planning assistant. Your task is to produce a structured task list
            for a software project based on the provided input documents and approved plans.

            Each task must include ALL of the following fields in this exact markdown format:

            ## Task: <ID>
            **Title:** <short imperative summary>
            **Description:** <what must be implemented and why>
            **Dependencies:** <comma-separated task IDs, or "none">
            **Implementation Plan:**
            <step-by-step approach>
            **Testing Plan:**
            <which tests to write and what they must verify>
            **Acceptance Criteria:**
            <observable, verifiable conditions for the task to be done>
            **Sample Code:**
            <illustrative snippets where helpful; omit if not applicable>
            **References:**
            <links or references to relevant sections of the input documents>

            Use sequential IDs like TASK-001, TASK-002, etc.
            Start the document with "# Task List" as a top-level heading.
            Every task must be completable in a single pull request.
            Declare dependencies explicitly so the orchestrator can determine the correct execution order.
        """.trimIndent()

        val userContent = buildString {
            appendLine(inputDocs)
            appendLine()
            appendLine("---")
            appendLine("## Approved High-Level Plan")
            appendLine()
            appendLine(highLevelPlan)
            appendLine()
            appendLine("---")
            appendLine("## Approved Low-Level Plan")
            appendLine()
            appendLine(lowLevelPlan)
        }

        val content = callAi(systemPrompt, userContent)
        Files.writeString(taskListFile, content)
        logI(TAG, "Task list written to $taskListFile")
        return PlanDocument(PlanningStage.TASK_LIST, taskListFile)
    }

    override suspend fun revise(stage: PlanningStage): PlanDocument {
        logI(TAG, "Revising stage: $stage")
        val file = fileForStage(stage)
        val annotatedContent = Files.readString(file)

        val systemPrompt = """
            You are a technical planning assistant. The document below is a draft that has been
            annotated by a human reviewer with feedback, questions, and requested changes.
            The annotations may appear as inline comments, notes, or edited text.

            Produce a revised version of the document that fully incorporates the reviewer's
            feedback. Remove all annotation markers from the final output — the result should
            be a clean document with no reviewer notes remaining.

            Preserve the original document structure and title. Output only the revised document.
        """.trimIndent()

        val content = callAi(systemPrompt, annotatedContent)
        Files.writeString(file, content)
        logI(TAG, "Revised ${file.fileName} written")
        return PlanDocument(stage, file)
    }

    override fun approve(stage: PlanningStage) {
        val markerFile = approvalMarkerForStage(stage)
        Files.writeString(markerFile, "approved at ${System.currentTimeMillis()}")
        logI(TAG, "Approval marker written: $markerFile")
    }

    private fun fileForStage(stage: PlanningStage): Path = when (stage) {
        PlanningStage.HIGH_LEVEL_PLAN -> highLevelPlanFile
        PlanningStage.LOW_LEVEL_PLAN -> lowLevelPlanFile
        PlanningStage.TASK_LIST -> taskListFile
    }

    private fun approvalMarkerForStage(stage: PlanningStage): Path = when (stage) {
        PlanningStage.HIGH_LEVEL_PLAN -> highLevelPlanApproved
        PlanningStage.LOW_LEVEL_PLAN -> lowLevelPlanApproved
        PlanningStage.TASK_LIST -> taskListApproved
    }

    private fun readInputDocs(): String {
        val docs = documentStore.getAll()
        return buildString {
            docs.forEach { doc ->
                val filePath = docsDir.resolve(doc.relativePath)
                if (Files.exists(filePath)) {
                    appendLine("---")
                    appendLine("## ${doc.type.name.replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() }}")
                    appendLine()
                    appendLine(Files.readString(filePath))
                    appendLine()
                }
            }
        }
    }

    private suspend fun callAi(systemPrompt: String, userContent: String): String {
        val response = aiProvider.chat(
            model = model,
            systemPrompt = systemPrompt,
            messages = listOf(AiMessage("user", userContent)),
            tools = emptyList(),
        )
        val text = response.content.filterIsInstance<AiContentBlock.Text>().firstOrNull()?.text
            ?: error("AI returned no text content")
        return text.trim()
    }
}

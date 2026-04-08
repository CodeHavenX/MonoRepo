package com.cramsan.agentic.core

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WorkflowConfig(
    val stages: List<WorkflowStageConfig> = defaultWorkflowStages(),
)

@Serializable
data class WorkflowStageConfig(
    val id: String,
    val name: String,
    val outputFile: String,
    val approvalMarkerFile: String,
    val requiresApproval: Boolean = true,
    val inputDependencies: List<String> = emptyList(),
    val prompt: WorkflowPromptConfig,
)

@Serializable
sealed class WorkflowPromptConfig {
    @Serializable
    @SerialName("inline")
    data class Inline(val systemPrompt: String) : WorkflowPromptConfig()

    @Serializable
    @SerialName("file")
    data class File(val path: String) : WorkflowPromptConfig()
}

fun defaultWorkflowStages(): List<WorkflowStageConfig> = listOf(
    WorkflowStageConfig(
        id = "stage1",
        name = "High-Level Plan",
        outputFile = "high-level-plan.md",
        approvalMarkerFile = "high-level-plan.approved",
        requiresApproval = true,
        inputDependencies = emptyList(),
        prompt = WorkflowPromptConfig.Inline(
            systemPrompt = """
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
        ),
    ),
    WorkflowStageConfig(
        id = "stage2",
        name = "Low-Level Plan",
        outputFile = "low-level-plan.md",
        approvalMarkerFile = "low-level-plan.approved",
        requiresApproval = true,
        inputDependencies = listOf("stage1"),
        prompt = WorkflowPromptConfig.Inline(
            systemPrompt = """
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
        ),
    ),
    WorkflowStageConfig(
        id = "stage3",
        name = "Task List",
        outputFile = "task-list.md",
        approvalMarkerFile = "task-list.approved",
        requiresApproval = true,
        inputDependencies = listOf("stage2"),
        prompt = WorkflowPromptConfig.Inline(
            systemPrompt = """
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
        ),
    ),
)

/**
 * System prompt used when revising a stage document based on human feedback.
 */
const val REVISION_SYSTEM_PROMPT = """
You are a technical planning assistant. The document below is a draft that has been
annotated by a human reviewer with feedback, questions, and requested changes.
The annotations may appear as inline comments, notes, or edited text.

Produce a revised version of the document that fully incorporates the reviewer's
feedback. Remove all annotation markers from the final output — the result should
be a clean document with no reviewer notes remaining.

Preserve the original document structure and title. Output only the revised document.
"""

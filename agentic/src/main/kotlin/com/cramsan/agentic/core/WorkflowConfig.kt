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
    val requiresApproval: Boolean = true,
    val inputDependencies: List<String> = emptyList(),
    val prompt: WorkflowPromptConfig,
) {
    /**
     * Approval marker file path, derived from the stage ID.
     * Internal implementation detail - not exposed in user configuration.
     */
    val approvalMarkerFile: String
        get() = "${id}.approved"
}

@Serializable
sealed class WorkflowPromptConfig {
    @Serializable
    @SerialName("inline")
    data class Inline(val systemPrompt: String) : WorkflowPromptConfig()

    @Serializable
    @SerialName("file")
    data class File(val path: String) : WorkflowPromptConfig()
}

/**
 * Returns the default workflow stages configuration.
 * Stage prompts are stored in resources/templates/workflow/ and referenced by path.
 */
fun defaultWorkflowStages(): List<WorkflowStageConfig> = listOf(
    WorkflowStageConfig(
        id = "stage1",
        name = "High-Level Plan",
        outputFile = "high-level-plan.md",
        requiresApproval = true,
        inputDependencies = emptyList(),
        prompt = WorkflowPromptConfig.File(path = "templates/workflow/stage1-high-level-plan.md"),
    ),
    WorkflowStageConfig(
        id = "stage2",
        name = "Low-Level Plan",
        outputFile = "low-level-plan.md",
        requiresApproval = true,
        inputDependencies = listOf("stage1"),
        prompt = WorkflowPromptConfig.File(path = "templates/workflow/stage2-low-level-plan.md"),
    ),
    WorkflowStageConfig(
        id = "stage3",
        name = "Task List",
        outputFile = "task-list.md",
        requiresApproval = true,
        inputDependencies = listOf("stage2"),
        prompt = WorkflowPromptConfig.File(path = "templates/workflow/stage3-task-list.md"),
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

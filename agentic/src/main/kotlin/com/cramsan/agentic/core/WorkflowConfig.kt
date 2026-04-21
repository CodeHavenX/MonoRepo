package com.cramsan.agentic.core

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Top-level planning workflow configuration stored in `planning.json`. Defines the ordered
 * sequence of stages the AI executes during `agentic plan`. Defaults to the four built-in
 * stages (document review → high-level plan → low-level plan → task list).
 */
@Serializable
data class WorkflowConfig(
    val stages: List<WorkflowStageConfig> = defaultWorkflowStages(),
)

/**
 * Configuration for a single planning workflow stage. Stages are executed in list order;
 * [inputDependencies] lists the IDs of other stages that must be approved before this one can start.
 *
 * [outputFile] is relative to the docs directory. [approvalRecordFile] is derived from [id]
 * and written to `.agentic-meta/` when the stage is approved via
 * [com.cramsan.agentic.input.WorkflowService.approveStage].
 */
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
     * Approval record file path within .agentic-meta/, derived from the stage ID.
     * Internal implementation detail - not exposed in user configuration.
     */
    val approvalRecordFile: String
        get() = ".agentic-meta/stage.$id.json"
}

/**
 * Source of the system prompt for a [WorkflowStageConfig]. Either an inline string or
 * a reference to a classpath resource file.
 */
@Serializable
sealed class WorkflowPromptConfig {
    /** System prompt embedded directly in the configuration. */
    @Serializable
    @SerialName("inline")
    data class Inline(val systemPrompt: String) : WorkflowPromptConfig()

    /** System prompt loaded from a classpath resource at the given [path]. */
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
        id = "stage0",
        name = "Document Review",
        outputFile = "document-review-report.md",
        requiresApproval = true,
        inputDependencies = emptyList(),
        prompt = WorkflowPromptConfig.File(path = "templates/workflow/stage0-document-review.md"),
    ),
    WorkflowStageConfig(
        id = "stage1",
        name = "High-Level Plan",
        outputFile = "high-level-plan.md",
        requiresApproval = true,
        inputDependencies = listOf("stage0"),
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

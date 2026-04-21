package com.cramsan.agentic.input

import com.cramsan.agentic.core.StageDocument
import com.cramsan.agentic.core.WorkflowConfigError
import com.cramsan.agentic.core.WorkflowStageConfig
import com.cramsan.agentic.core.WorkflowState

/**
 * Manages the human-in-the-loop AI planning pipeline that converts raw project documents
 * into an actionable task list.
 *
 * The workflow proceeds through a sequence of [com.cramsan.agentic.core.WorkflowStageConfig]s
 * defined in `planning.json`. Each stage:
 * 1. Is started by the operator (`agentic plan start`) — AI generates the output document.
 * 2. May be revised (`agentic plan revise`) — human annotates, AI produces a clean version.
 * 3. Is approved by the operator (`agentic plan approve`) — writes an approval record to disk.
 *
 * Approval of the final stage (typically `stage3` / task list) enables `agentic run start`.
 * Only used during the planning phase; the orchestrator execution phase does not interact
 * with this service at runtime.
 */
interface WorkflowService {
    /**
     * Returns the current state of the workflow, including status and stage information.
     */
    fun getState(): WorkflowState

    /**
     * Returns the configuration for a specific stage by ID, or null if not found.
     */
    fun getStageConfig(stageId: String): WorkflowStageConfig?

    /**
     * Returns all configured stages in order.
     */
    fun getAllStages(): List<WorkflowStageConfig>

    /**
     * Starts a specific stage by ID, generating its output document.
     * @throws IllegalArgumentException if the stage ID is not found
     * @throws IllegalStateException if dependencies are not met
     */
    suspend fun startStage(stageId: String): StageDocument

    /**
     * Starts the next pending stage in the workflow.
     * @return the generated document, or null if no stage is ready to start
     */
    suspend fun startNextStage(): StageDocument?

    /**
     * Revises a stage document based on human feedback annotations.
     * @throws IllegalArgumentException if the stage ID is not found
     */
    suspend fun reviseStage(stageId: String): StageDocument

    /**
     * Approves a stage, marking it as complete and allowing dependent stages to proceed.
     * @throws IllegalArgumentException if the stage ID is not found
     */
    fun approveStage(stageId: String)

    /**
     * Validates the workflow configuration for errors such as circular dependencies,
     * missing dependencies, or duplicate stage IDs.
     * @return a list of configuration errors, empty if valid
     */
    fun validateWorkflowConfig(): List<WorkflowConfigError>
}

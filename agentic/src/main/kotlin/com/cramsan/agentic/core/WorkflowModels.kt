package com.cramsan.agentic.core

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.nio.file.Path

@Serializable
sealed class WorkflowStatus {
    @Serializable
    @SerialName("not_started")
    data object NotStarted : WorkflowStatus()

    @Serializable
    @SerialName("stage_in_progress")
    data class StageInProgress(val stageId: String) : WorkflowStatus()

    @Serializable
    @SerialName("stage_pending_approval")
    data class StagePendingApproval(val stageId: String) : WorkflowStatus()

    @Serializable
    @SerialName("complete")
    data object Complete : WorkflowStatus()
}

data class WorkflowState(
    val status: WorkflowStatus,
    val completedStages: List<String>,
    val currentStage: WorkflowStageConfig?,
    val nextStage: WorkflowStageConfig?,
)

data class StageDocument(
    val stageId: String,
    val stageName: String,
    val path: Path,
)

data class WorkflowConfigError(
    val type: WorkflowConfigErrorType,
    val message: String,
    val stageId: String? = null,
)

enum class WorkflowConfigErrorType {
    CIRCULAR_DEPENDENCY,
    MISSING_DEPENDENCY,
    DUPLICATE_STAGE_ID,
    INVALID_OUTPUT_FILE,
}

package com.cramsan.agentic.core

import kotlinx.serialization.Serializable
import java.nio.file.Path

@Serializable
enum class PlanningStatus {
    NOT_STARTED,
    AWAITING_DOCUMENT_VALIDATION,
    STAGE_1_IN_PROGRESS,
    STAGE_1_PENDING_APPROVAL,
    STAGE_2_IN_PROGRESS,
    STAGE_2_PENDING_APPROVAL,
    STAGE_3_IN_PROGRESS,
    STAGE_3_PENDING_APPROVAL,
    COMPLETE,
}

enum class PlanningStage(val label: String) {
    HIGH_LEVEL_PLAN("stage1"),
    LOW_LEVEL_PLAN("stage2"),
    TASK_LIST("stage3"),
}

data class PlanDocument(
    val stage: PlanningStage,
    val path: Path,
)

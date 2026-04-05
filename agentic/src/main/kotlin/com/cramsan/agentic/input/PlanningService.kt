package com.cramsan.agentic.input

import com.cramsan.agentic.core.PlanDocument
import com.cramsan.agentic.core.PlanningStage
import com.cramsan.agentic.core.PlanningStatus

interface PlanningService {
    fun status(): PlanningStatus
    suspend fun generateHighLevelPlan(): PlanDocument
    suspend fun generateLowLevelPlan(): PlanDocument
    suspend fun generateTaskList(): PlanDocument
    suspend fun revise(stage: PlanningStage): PlanDocument
    fun approve(stage: PlanningStage)
}

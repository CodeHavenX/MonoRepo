package com.cramsan.agentic.core

import kotlinx.serialization.Serializable

/**
 * Configuration for how planning is executed - defines the workflow stages,
 * input documents, and reviewers for a project.
 */
@Serializable
data class PlanningConfig(
    val inputDocuments: List<InputDocumentConfig> = defaultInputDocuments(),
    val workflow: WorkflowConfig = WorkflowConfig(),
    val reviewers: ReviewersConfig = defaultReviewers(),
)

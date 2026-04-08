package com.cramsan.agentic.core

import kotlinx.serialization.Serializable

@Serializable
data class AgenticDocument(
    val id: String,
    val typeId: String,
    @Deprecated("Use typeId instead. Kept for backwards compatibility.")
    val type: DocumentType? = null,
    val relativePath: String,
    val status: DocumentStatus,
    val lastModifiedEpochMs: Long,
)

@Serializable
enum class DocumentType {
    GOALS_SCOPE,
    ARCHITECTURE_DESIGN,
    STANDARDS,
    TASK_LIST,
    HIGH_LEVEL_PLAN,
    LOW_LEVEL_PLAN,
}

@Serializable
enum class DocumentStatus {
    UNREVIEWED,
    IN_REVIEW,
    NEEDS_REVISION,
    VALIDATED,
}

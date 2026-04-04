package com.cramsan.agentic.core

import kotlinx.serialization.Serializable

@Serializable
data class AgenticDocument(
    val id: String,
    val type: DocumentType,
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
}

@Serializable
enum class DocumentStatus {
    UNREVIEWED,
    IN_REVIEW,
    NEEDS_REVISION,
    VALIDATED,
}

package com.cramsan.agentic.core

import kotlinx.serialization.Serializable

/**
 * Metadata record for a planning-phase document (goals, architecture, task list, etc.) tracked
 * by [com.cramsan.agentic.input.DocumentStore]. The document content itself lives on disk at
 * [relativePath] relative to the docs directory; this record only holds lifecycle metadata.
 *
 * When any tracked document is modified on disk, [DocumentStore.onDocumentChanged] resets
 * **all** documents to [DocumentStatus.UNREVIEWED], forcing a full re-validation pass.
 */
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

/**
 * Classifies the purpose of a planning document. Used to populate [AgenticDocument.type]
 * in legacy records; new code should use the string [AgenticDocument.typeId] instead.
 */
@Serializable
enum class DocumentType {
    GOALS_SCOPE,
    ARCHITECTURE_DESIGN,
    STANDARDS,
    TASK_LIST,
    HIGH_LEVEL_PLAN,
    LOW_LEVEL_PLAN,
}

/**
 * Validation lifecycle of a [AgenticDocument] as managed by [com.cramsan.agentic.input.ValidationService].
 *
 * - [UNREVIEWED]: default state; document has not been evaluated in the current validation pass.
 * - [IN_REVIEW]: [com.cramsan.agentic.input.ValidationService.reviewDocument] is actively running.
 * - [NEEDS_REVISION]: AI validation found at least one [com.cramsan.agentic.core.IssueSeverity.BLOCKING] issue.
 * - [VALIDATED]: no blocking issues remain; document is approved to proceed to the next workflow stage.
 */
@Serializable
enum class DocumentStatus {
    UNREVIEWED,
    IN_REVIEW,
    NEEDS_REVISION,
    VALIDATED,
}

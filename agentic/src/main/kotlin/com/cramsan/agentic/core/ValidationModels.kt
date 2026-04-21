package com.cramsan.agentic.core

import kotlinx.serialization.Serializable

/**
 * A single issue raised by the AI during a document validation pass. Issues are grouped into
 * a [ValidationReport] and persisted to `docs/validation-report.md`.
 *
 * Only [IssueSeverity.BLOCKING] issues prevent a document from reaching
 * [com.cramsan.agentic.core.DocumentStatus.VALIDATED].
 */
@Serializable
data class ValidationIssue(
    val id: String,
    val documentId: String,
    val description: String,
    val severity: IssueSeverity,
    val status: IssueStatus,
)

/**
 * Determines whether a [ValidationIssue] blocks workflow progression.
 * [BLOCKING] issues must be resolved before the relevant document can be approved.
 * [ADVISORY] issues are informational and do not gate stage approval.
 */
@Serializable
enum class IssueSeverity { BLOCKING, ADVISORY }

/**
 * Lifecycle of a [ValidationIssue]. Currently informational; the system does not automatically
 * transition issues from [OPEN] to [ADDRESSED] — that requires human review.
 */
@Serializable
enum class IssueStatus { OPEN, ADDRESSED, DISMISSED }

/**
 * Snapshot of all [ValidationIssue]s found during a single validation run. Serialized as JSON
 * and written to `docs/validation-report.md` after each [com.cramsan.agentic.input.ValidationService.runValidationPass].
 * A new report overwrites the previous one; history is not retained.
 */
@Serializable
data class ValidationReport(
    val runId: String,
    val timestampEpochMs: Long,
    val issues: List<ValidationIssue>,
)

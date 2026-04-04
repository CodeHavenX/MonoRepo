package com.cramsan.agentic.core

import kotlinx.serialization.Serializable

@Serializable
data class ValidationIssue(
    val id: String,
    val documentId: String,
    val description: String,
    val severity: IssueSeverity,
    val status: IssueStatus,
)

@Serializable
enum class IssueSeverity { BLOCKING, ADVISORY }

@Serializable
enum class IssueStatus { OPEN, ADDRESSED, DISMISSED }

@Serializable
data class ValidationReport(
    val runId: String,
    val timestampEpochMs: Long,
    val issues: List<ValidationIssue>,
)

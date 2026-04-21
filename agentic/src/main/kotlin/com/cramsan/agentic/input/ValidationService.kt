package com.cramsan.agentic.input

import com.cramsan.agentic.core.AgenticDocument
import com.cramsan.agentic.core.ValidationIssue
import com.cramsan.agentic.core.ValidationReport

/**
 * Orchestrates AI-powered review of planning documents during the `agentic plan` phase.
 *
 * **[runValidationPass]** is the primary entry point: it iterates all documents tracked by
 * [com.cramsan.agentic.input.DocumentStore], calls [reviewDocument] on each, updates document
 * statuses, and persists a [com.cramsan.agentic.core.ValidationReport] to disk.
 *
 * **[reviewDocument]** evaluates a single document and returns the issues found. It may be
 * called directly for targeted re-validation, but [runValidationPass] is preferred for a full pass.
 *
 * After the primary AI validation, reviewer agents ([com.cramsan.agentic.reviewer.ReviewerAgent])
 * are run in parallel for additional perspective. Their feedback is printed to the console but
 * does not affect document status or the validation report.
 */
interface ValidationService {
    /**
     * Sends [document] to the AI for review and returns any [com.cramsan.agentic.core.ValidationIssue]s
     * found. Does not update [DocumentStore] state — callers are responsible for applying status changes.
     */
    suspend fun reviewDocument(document: AgenticDocument): List<ValidationIssue>

    /**
     * Runs a full validation pass across all documents tracked by [DocumentStore]. Updates each
     * document's status and persists the combined [com.cramsan.agentic.core.ValidationReport].
     */
    suspend fun runValidationPass(): ValidationReport
}

package com.cramsan.agentic.core

import kotlinx.serialization.Serializable

/**
 * Persisted approval receipt for a completed workflow stage. Written to
 * `.agentic-meta/stage.{stageId}.json` by [com.cramsan.agentic.input.WorkflowService.approveStage].
 *
 * [inputHashes] captures a SHA-256 hash of each input document at approval time. If any input
 * is modified after approval, [com.cramsan.agentic.input.WorkflowService.getState] emits a
 * [com.cramsan.agentic.core.StageApprovalWarning] to alert the user that downstream stages may
 * need to be re-generated.
 *
 * The presence of this file — not its content — is the primary gate checked by `agentic run start`
 * to verify that the planning phase is complete.
 */
@Serializable
data class StageApprovalRecord(
    val stageId: String,
    val approvedAtEpochMs: Long,
    val inputHashes: Map<String, String>,
)

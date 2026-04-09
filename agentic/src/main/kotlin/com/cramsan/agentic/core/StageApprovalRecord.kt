package com.cramsan.agentic.core

import kotlinx.serialization.Serializable

@Serializable
data class StageApprovalRecord(
    val stageId: String,
    val approvedAtEpochMs: Long,
    val inputHashes: Map<String, String>,
)

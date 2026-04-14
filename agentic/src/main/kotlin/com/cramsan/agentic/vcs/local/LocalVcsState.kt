package com.cramsan.agentic.vcs.local

import com.cramsan.agentic.core.PullRequestState
import kotlinx.serialization.Serializable

@Serializable
data class LocalPrState(
    val prs: MutableList<LocalPr> = mutableListOf(),
    var nextPrId: Int = 1,
)

@Serializable
data class LocalPr(
    val id: String,
    val sourceBranch: String,
    val targetBranch: String,
    val title: String,
    val body: String,
    var state: PullRequestState,
    val labels: List<String>,
    val comments: MutableList<LocalComment> = mutableListOf(),
    var hasRequestedChanges: Boolean = false,
)

@Serializable
data class LocalComment(
    val author: String,
    val body: String,
    val createdAtEpochMs: Long,
)

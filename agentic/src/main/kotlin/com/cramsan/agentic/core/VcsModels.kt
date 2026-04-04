package com.cramsan.agentic.core

import kotlinx.serialization.Serializable

@Serializable
data class PullRequest(
    val id: String,
    val url: String,
    val title: String,
    val state: PullRequestState,
    val sourceBranch: String,
    val targetBranch: String,
    val labels: List<String>,
)

@Serializable
enum class PullRequestState { OPEN, CLOSED, MERGED }

@Serializable
data class PullRequestComment(
    val author: String,
    val body: String,
    val createdAtEpochMs: Long,
)

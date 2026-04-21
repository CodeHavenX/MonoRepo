package com.cramsan.agentic.vcs.local

import com.cramsan.agentic.core.PullRequestState
import kotlinx.serialization.Serializable

/**
 * Root serialized state for [com.cramsan.agentic.vcs.local.LocalVcsProvider], persisted as JSON
 * to the configured `stateFile`. [nextPrId] is a monotonically incrementing counter; it is never
 * reset, so PR IDs remain unique across restarts even if old PRs are deleted from [prs].
 */
@Serializable
data class LocalPrState(
    val prs: MutableList<LocalPr> = mutableListOf(),
    var nextPrId: Int = 1,
)

/**
 * In-process representation of a pull request managed by [com.cramsan.agentic.vcs.local.LocalVcsProvider].
 * Mutable fields ([state], [hasRequestedChanges]) are updated in place within the state file.
 */
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

/** A comment stored within a [LocalPr]. Mirrors [com.cramsan.agentic.core.PullRequestComment]. */
@Serializable
data class LocalComment(
    val author: String,
    val body: String,
    val createdAtEpochMs: Long,
)

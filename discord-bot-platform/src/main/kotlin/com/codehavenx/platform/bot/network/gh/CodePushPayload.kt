package com.codehavenx.platform.bot.network.gh

import kotlinx.serialization.SerialName

data class CodePushPayload(
    val ref: String? = null,
    val before: String? = null,
    val after: String? = null,
    val repository: Repository? = null,
    val pusher: Pusher? = null,
    val organization: Organization? = null,
    val sender: Sender? = null,
    val created: Boolean = false,
    val deleted: Boolean = false,
    val forced: Boolean = false,
    @SerialName("base_ref")
    val baseRef: String? = null,
    val compare: String? = null,
    val commits: ArrayList<Commit>? = null,
    @SerialName("head_commit")
    val headCommit: HeadCommit? = null,
)

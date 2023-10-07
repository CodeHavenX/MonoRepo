package com.codehavenx.platform.bot.network.gh

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class Commit(
    val id: String? = null,
    @SerialName("tree_id")
    val treeId: String? = null,
    val distinct: Boolean = false,
    val message: String? = null,
    @Contextual
    val timestamp: Date? = null,
    val url: String? = null,
    val author: Author? = null,
    val committer: Committer? = null,
    val added: ArrayList<String>? = null,
    val removed: ArrayList<String>? = null,
    val modified: ArrayList<String>? = null,
)

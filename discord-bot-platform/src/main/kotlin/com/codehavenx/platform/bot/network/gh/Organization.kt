package com.codehavenx.platform.bot.network.gh

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Organization(
    @SerialName("login")
    val login: String? = null,
    @SerialName("id")
    val id: Long = 0,
    @SerialName("node_id")
    val nodeId: String? = null,
    @SerialName("url")
    val url: String? = null,
    @SerialName("repos_url")
    val reposUrl: String? = null,
    @SerialName("events_url")
    val eventsUrl: String? = null,
    @SerialName("hooks_url")
    val hooksUrl: String? = null,
    @SerialName("issues_url")
    val issuesUrl: String? = null,
    @SerialName("members_url")
    val membersUrl: String? = null,
    @SerialName("public_members_url")
    val publicMembersUrl: String? = null,
    @SerialName("avatar_url")
    val avatarUrl: String? = null,
    @SerialName("description")
    val description: String? = null,
)

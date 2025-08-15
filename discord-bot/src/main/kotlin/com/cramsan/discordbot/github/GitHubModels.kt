package com.cramsan.discordbot.github

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a GitHub issue.
 */
@Serializable
data class GitHubIssue(
    val id: Long,
    val number: Int,
    val title: String,
    val body: String?,
    val state: String,
    val user: GitHubUser,
    val labels: List<GitHubLabel>,
    @SerialName("html_url")
    val htmlUrl: String,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("updated_at")
    val updatedAt: String
)

/**
 * Represents a GitHub user.
 */
@Serializable
data class GitHubUser(
    val login: String,
    val id: Long,
    @SerialName("avatar_url")
    val avatarUrl: String?,
    @SerialName("html_url")
    val htmlUrl: String
)

/**
 * Represents a GitHub label.
 */
@Serializable
data class GitHubLabel(
    val id: Long,
    val name: String,
    val color: String,
    val description: String?
)

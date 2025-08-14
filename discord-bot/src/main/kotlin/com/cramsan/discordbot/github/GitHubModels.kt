package com.cramsan.discordbot.github

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
    val html_url: String,
    val created_at: String,
    val updated_at: String
)

/**
 * Represents a GitHub user.
 */
@Serializable
data class GitHubUser(
    val login: String,
    val id: Long,
    val avatar_url: String?,
    val html_url: String
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
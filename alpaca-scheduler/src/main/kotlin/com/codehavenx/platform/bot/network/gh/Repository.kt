package com.codehavenx.platform.bot.network.gh

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class Repository(
    @SerialName("id")
    val id: Long = 0,
    @SerialName("node_id")
    val nodeId: String? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("full_name")
    val fullName: String? = null,
    @SerialName("private")
    val private: Boolean = false,
    @SerialName("owner")
    val owner: Owner? = null,
    @SerialName("html_url")
    val htmlUrl: String? = null,
    @SerialName("description")
    val description: String? = null,
    @SerialName("fork")
    val fork: Boolean = false,
    @SerialName("url")
    val url: String? = null,
    @SerialName("forks_url")
    val forksUrl: String? = null,
    @SerialName("keys_url")
    val keysUrl: String? = null,
    @SerialName("collaborators_url")
    val collaboratorsUrl: String? = null,
    @SerialName("teams_url")
    val teamsUrl: String? = null,
    @SerialName("hooks_url")
    val hooksUrl: String? = null,
    @SerialName("issue_events_url")
    val issueEventsUrl: String? = null,
    @SerialName("events_url")
    val eventsUrl: String? = null,
    @SerialName("assignees_url")
    val assigneesUrl: String? = null,
    @SerialName("branches_url")
    val branchesUrl: String? = null,
    @SerialName("tags_url")
    val tagsUrl: String? = null,
    @SerialName("blobs_url")
    val blobsUrl: String? = null,
    @SerialName("git_tags_url")
    val gitTagsUrl: String? = null,
    @SerialName("git_refs_url")
    val gitRefsUrl: String? = null,
    @SerialName("trees_url")
    val treesUrl: String? = null,
    @SerialName("statuses_url")
    val statusesUrl: String? = null,
    @SerialName("languages_url")
    val languagesUrl: String? = null,
    @SerialName("stargazers_url")
    val stargazersUrl: String? = null,
    @SerialName("contributors_url")
    val contributorsUrl: String? = null,
    @SerialName("subscribers_url")
    val subscribersUrl: String? = null,
    @SerialName("subscription_url")
    val subscriptionUrl: String? = null,
    @SerialName("commits_url")
    val commitsUrl: String? = null,
    @SerialName("git_commits_url")
    val gitCommitsUrl: String? = null,
    @SerialName("comments_url")
    val commentsUrl: String? = null,
    @SerialName("issue_comment_url")
    val issueCommentUrl: String? = null,
    @SerialName("contents_url")
    val contentsUrl: String? = null,
    @SerialName("compare_url")
    val compareUrl: String? = null,
    @SerialName("merges_url")
    val mergesUrl: String? = null,
    @SerialName("archive_url")
    val archiveUrl: String? = null,
    @SerialName("downloads_url")
    val downloadsUrl: String? = null,
    @SerialName("issues_url")
    val issuesUrl: String? = null,
    @SerialName("pulls_url")
    val pullsUrl: String? = null,
    @SerialName("milestones_url")
    val milestonesUrl: String? = null,
    @SerialName("notifications_url")
    val notificationsUrl: String? = null,
    @SerialName("labels_url")
    val labelsUrl: String? = null,
    @SerialName("releases_url")
    val releasesUrl: String? = null,
    @SerialName("deployments_url")
    val deploymentsUrl: String? = null,
    @SerialName("created_at")
    val createdAt: Long = 0,
    @SerialName("updated_at")
    @Contextual
    val updatedAt: Date? = null,
    @SerialName("pushed_at")
    val pushedAt: Long = 0,
    @SerialName("git_url")
    val gitUrl: String? = null,
    @SerialName("ssh_url")
    val sshUrl: String? = null,
    @SerialName("clone_url")
    val cloneUrl: String? = null,
    @SerialName("svn_url")
    val svnUrl: String? = null,
    @SerialName("homepage")
    val homepage: String? = null,
    @SerialName("size")
    val size: Long = 0,
    @SerialName("stargazers_count")
    val stargazersCount: Long = 0,
    @SerialName("watchers_count")
    val watchersCount: Long = 0,
    @SerialName("language")
    val language: String? = null,
    @SerialName("has_issues")
    val hasIssues: Boolean = false,
    @SerialName("has_projects")
    val hasProjects: Boolean = false,
    @SerialName("has_downloads")
    val hasDownloads: Boolean = false,
    @SerialName("has_wiki")
    val hasWiki: Boolean = false,
    @SerialName("has_pages")
    val hasPages: Boolean = false,
    @SerialName("has_discussions")
    val hasDiscussions: Boolean = false,
    @SerialName("forks_count")
    val forksCount: Long = 0,
    @SerialName("mirror_url")
    val mirrorUrl: String? = null,
    @SerialName("archived")
    val archived: Boolean = false,
    @SerialName("disabled")
    val disabled: Boolean = false,
    @SerialName("open_issues_count")
    val openIssuesCount: Long = 0,
    @SerialName("license")
    val license: String? = null,
    @SerialName("allow_forking")
    val allowForking: Boolean = false,
    @SerialName("is_template")
    val isTemplate: Boolean = false,
    @SerialName("web_commit_signoff_required")
    val webCommitSignoffRequired: Boolean = false,
    @SerialName("topics")
    val topics: ArrayList<String>? = null,
    @SerialName("visibility")
    val visibility: String? = null,
    @SerialName("forks")
    val forks: Long = 0,
    @SerialName("open_issues")
    val openIssues: Long = 0,
    @SerialName("watchers")
    val watchers: Long = 0,
    @SerialName("default_branch")
    val defaultBranch: String? = null,
    @SerialName("stargazers")
    val stargazers: Long = 0,
    @SerialName("master_branch")
    val masterBranch: String? = null,
    @SerialName("organization")
    val organization: String? = null,
)

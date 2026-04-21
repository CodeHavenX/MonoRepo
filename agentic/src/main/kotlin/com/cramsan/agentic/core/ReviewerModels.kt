package com.cramsan.agentic.core

/**
 * Configuration for a reviewer agent loaded from a `.md` file in the `docs/reviewers/` directory.
 * The file name (without extension) becomes [name]; the full file content becomes [systemPrompt].
 *
 * Not serialized — reviewer definitions are transient, loaded fresh at each invocation by
 * [com.cramsan.agentic.reviewer.ReviewerLoader].
 */
data class ReviewerDefinition(
    val name: String,
    val systemPrompt: String,
)

/**
 * The markdown-formatted feedback produced by a [com.cramsan.agentic.reviewer.ReviewerAgent].
 * During document review, feedback is printed to the console. During code review, it is posted
 * as a PR comment via [com.cramsan.agentic.vcs.VcsProvider.addPullRequestComment].
 */
data class ReviewerFeedback(
    val reviewerName: String,
    val content: String,
)

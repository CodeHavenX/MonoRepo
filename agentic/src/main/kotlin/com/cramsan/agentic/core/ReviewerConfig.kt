package com.cramsan.agentic.core

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Configuration for how reviewers are loaded.
 */
@Serializable
sealed class ReviewersConfig {
    /**
     * Load reviewers from markdown files in a directory.
     * Each .md file becomes a reviewer where the filename (minus extension) is the ID
     * and the file content is the system prompt.
     *
     * @property path Path to the reviewers directory. Can be relative to docsDir or absolute.
     */
    @Serializable
    @SerialName("directory")
    data class Directory(val path: String = "reviewers/") : ReviewersConfig()

    /**
     * Define reviewers inline in the configuration.
     */
    @Serializable
    @SerialName("inline")
    data class Inline(val reviewers: List<ReviewerConfig>) : ReviewersConfig()
}

/**
 * Configuration for a single reviewer agent.
 *
 * @property id Unique identifier for the reviewer
 * @property name Human-readable name for the reviewer
 * @property prompt System prompt configuration
 */
@Serializable
data class ReviewerConfig(
    val id: String,
    val name: String,
    val prompt: ReviewerPromptConfig,
)

/**
 * System prompt configuration for a reviewer.
 */
@Serializable
sealed class ReviewerPromptConfig {
    /**
     * Inline system prompt.
     */
    @Serializable
    @SerialName("inline")
    data class Inline(val systemPrompt: String) : ReviewerPromptConfig()

    /**
     * File-based system prompt. Path can be relative to docsDir or absolute.
     */
    @Serializable
    @SerialName("file")
    data class File(val path: String) : ReviewerPromptConfig()
}

/**
 * Returns the default reviewers configuration matching the original hardcoded values.
 * Reviewer prompts are stored in resources/templates/reviewers/ and referenced by path.
 */
fun defaultReviewers(): ReviewersConfig = ReviewersConfig.Inline(
    reviewers = listOf(
        ReviewerConfig(
            id = "security",
            name = "Security Reviewer",
            prompt = ReviewerPromptConfig.File(path = "templates/reviewers/security.md"),
        ),
        ReviewerConfig(
            id = "design-patterns",
            name = "Design Patterns Reviewer",
            prompt = ReviewerPromptConfig.File(path = "templates/reviewers/design-patterns.md"),
        ),
    )
)

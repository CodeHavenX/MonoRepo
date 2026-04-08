package com.cramsan.agentic.reviewer

import com.cramsan.agentic.core.ReviewerDefinition
import com.cramsan.agentic.core.ReviewerPromptConfig
import com.cramsan.agentic.core.ReviewersConfig
import com.cramsan.agentic.core.resolvePath
import com.cramsan.framework.logging.logD
import com.cramsan.framework.logging.logI
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.extension
import kotlin.io.path.nameWithoutExtension

/**
 * A ReviewerLoader that supports both directory-based and inline reviewer configurations.
 *
 * - [ReviewersConfig.Directory]: Loads reviewers from .md files in the specified directory.
 * - [ReviewersConfig.Inline]: Uses reviewer definitions directly from configuration.
 */
class ConfigurableReviewerLoader(
    private val reviewersConfig: ReviewersConfig,
    private val docsDir: Path,
) : ReviewerLoader {

    override fun loadAll(): List<ReviewerDefinition> {
        return when (reviewersConfig) {
            is ReviewersConfig.Directory -> loadFromDirectory(reviewersConfig.path)
            is ReviewersConfig.Inline -> loadFromInlineConfig(reviewersConfig.reviewers)
        }
    }

    private fun loadFromDirectory(directoryPath: String): List<ReviewerDefinition> {
        val reviewersDir = resolvePath(docsDir, directoryPath)
        logI(TAG, "Loading reviewers from directory: $reviewersDir")

        if (!Files.exists(reviewersDir) || !Files.isDirectory(reviewersDir)) {
            logD(TAG, "Reviewers directory does not exist or is not a directory: $reviewersDir")
            return emptyList()
        }

        return Files.list(reviewersDir)
            .filter { it.extension == "md" }
            .map { file ->
                val name = file.nameWithoutExtension
                val systemPrompt = Files.readString(file)
                logD(TAG, "Loaded reviewer from file: $name")
                ReviewerDefinition(name = name, systemPrompt = systemPrompt)
            }
            .toList()
    }

    private fun loadFromInlineConfig(reviewers: List<com.cramsan.agentic.core.ReviewerConfig>): List<ReviewerDefinition> {
        logI(TAG, "Loading ${reviewers.size} reviewer(s) from inline configuration")

        return reviewers.map { config ->
            val systemPrompt = resolvePrompt(config.prompt)
            logD(TAG, "Loaded reviewer from config: ${config.name} (id=${config.id})")
            ReviewerDefinition(name = config.name, systemPrompt = systemPrompt)
        }
    }

    private fun resolvePrompt(prompt: ReviewerPromptConfig): String {
        return when (prompt) {
            is ReviewerPromptConfig.Inline -> prompt.systemPrompt
            is ReviewerPromptConfig.File -> {
                val promptPath = resolvePath(docsDir, prompt.path)
                logD(TAG, "Loading reviewer prompt from file: $promptPath")
                Files.readString(promptPath)
            }
        }
    }

    companion object {
        private const val TAG = "ConfigurableReviewerLoader"
    }
}

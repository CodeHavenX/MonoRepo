package com.cramsan.agentic.input

import com.cramsan.agentic.core.DocumentTemplateConfig
import com.cramsan.agentic.core.InputDocumentConfig
import com.cramsan.agentic.core.ReviewerConfig
import com.cramsan.agentic.core.ReviewerPromptConfig
import com.cramsan.agentic.core.ReviewersConfig
import com.cramsan.agentic.core.resolvePath
import com.cramsan.framework.logging.logI
import com.cramsan.framework.logging.logW
import java.nio.file.Files
import java.nio.file.Path

class DefaultScaffolder(
    private val inputDocuments: List<InputDocumentConfig>,
    private val reviewersConfig: ReviewersConfig,
) : Scaffolder {

    override fun scaffold(outputDir: Path) {
        // Scaffold input documents
        for (docConfig in inputDocuments) {
            val content = resolveTemplate(outputDir, docConfig.template)
            val filePath = resolvePath(outputDir, docConfig.filename)
            writeFile(filePath, content)
        }

        // Scaffold reviewers based on configuration
        when (reviewersConfig) {
            is ReviewersConfig.Inline -> scaffoldInlineReviewers(outputDir, reviewersConfig.reviewers)
            is ReviewersConfig.Directory -> {
                // Create the reviewers directory if it doesn't exist
                val reviewersDir = resolvePath(outputDir, reviewersConfig.path)
                if (!Files.exists(reviewersDir)) {
                    Files.createDirectories(reviewersDir)
                    logI(TAG, "Created reviewers directory: $reviewersDir")
                }
            }
        }
    }

    private fun scaffoldInlineReviewers(outputDir: Path, reviewers: List<ReviewerConfig>) {
        val reviewersDir = outputDir.resolve("reviewers")
        for (reviewer in reviewers) {
            val content = resolvePrompt(outputDir, reviewer.prompt)
            val filePath = reviewersDir.resolve("${reviewer.id}.md")
            writeFile(filePath, content)
        }
    }

    private fun resolveTemplate(baseDir: Path, template: DocumentTemplateConfig): String {
        return when (template) {
            is DocumentTemplateConfig.Inline -> template.content
            is DocumentTemplateConfig.File -> {
                val templatePath = resolvePath(baseDir, template.path)
                Files.readString(templatePath)
            }
        }
    }

    private fun resolvePrompt(baseDir: Path, prompt: ReviewerPromptConfig): String {
        return when (prompt) {
            is ReviewerPromptConfig.Inline -> prompt.systemPrompt
            is ReviewerPromptConfig.File -> {
                val promptPath = resolvePath(baseDir, prompt.path)
                Files.readString(promptPath)
            }
        }
    }

    private fun writeFile(path: Path, content: String) {
        if (Files.exists(path)) {
            logW(TAG, "File already exists, skipping: $path")
            return
        }
        Files.createDirectories(path.parent)
        Files.writeString(path, content)
        logI(TAG, "Scaffolded: $path")
    }

    companion object {
        private const val TAG = "DefaultScaffolder"
    }
}

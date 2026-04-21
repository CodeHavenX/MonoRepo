package com.cramsan.agentic.input

import com.cramsan.agentic.core.DocumentTemplateConfig
import com.cramsan.agentic.core.InputDocumentConfig
import com.cramsan.agentic.core.ReviewerConfig
import com.cramsan.agentic.core.ReviewerPromptConfig
import com.cramsan.agentic.core.ReviewersConfig
import com.cramsan.agentic.core.WorkflowPromptConfig
import com.cramsan.agentic.core.WorkflowStageConfig
import com.cramsan.agentic.core.resolvePath
import com.cramsan.framework.logging.logD
import com.cramsan.framework.logging.logI
import com.cramsan.framework.logging.logW
import java.nio.file.Files
import java.nio.file.Path

/**
 * Production [Scaffolder] that generates starter planning documents from embedded classpath
 * templates. Called once during `agentic init`.
 *
 * **Template resolution**: document templates are loaded from the JAR's `resources/templates/`
 * directory via [resolvePath]. Each [InputDocumentConfig] can reference a template file or
 * use an inline template string.
 *
 * **Idempotency**: if an output file already exists, it is not overwritten. This prevents
 * destroying user edits when `init` is run more than once.
 *
 * Reviewer templates are scaffolded into `docs/reviewers/` using [ReviewerConfig] definitions.
 * Workflow stage prompt files are copied to `docs/templates/workflow/`.
 */
class DefaultScaffolder(
    private val inputDocuments: List<InputDocumentConfig>,
    private val reviewersConfig: ReviewersConfig,
    private val workflowStages: List<WorkflowStageConfig> = emptyList(),
) : Scaffolder {

    override fun scaffold(outputDir: Path) {
        // Step 1: Copy all template files from resources to outputDir
        copyTemplatesFromResources(outputDir)

        // Step 2: Scaffold input documents from templates
        for (docConfig in inputDocuments) {
            val content = resolveTemplate(outputDir, docConfig.template)
            val filePath = resolvePath(outputDir, docConfig.filename)
            writeFile(filePath, content)
        }

        // Step 3: Scaffold reviewers based on configuration
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

    private fun copyTemplatesFromResources(outputDir: Path) {
        val templatePaths = collectDocumentTemplatePaths() +
            collectReviewerTemplatePaths() +
            collectWorkflowTemplatePaths()

        for (templatePath in templatePaths) {
            copyTemplateIfMissing(outputDir, templatePath)
        }
    }

    private fun collectDocumentTemplatePaths(): Set<String> =
        inputDocuments.mapNotNull { doc ->
            (doc.template as? DocumentTemplateConfig.File)?.path
        }.toSet()

    private fun collectReviewerTemplatePaths(): Set<String> {
        val inline = reviewersConfig as? ReviewersConfig.Inline ?: return emptySet()
        return inline.reviewers.mapNotNull { reviewer ->
            (reviewer.prompt as? ReviewerPromptConfig.File)?.path
        }.toSet()
    }

    private fun collectWorkflowTemplatePaths(): Set<String> =
        workflowStages.mapNotNull { stage ->
            (stage.prompt as? WorkflowPromptConfig.File)?.path
        }.toSet()

    private fun copyTemplateIfMissing(outputDir: Path, templatePath: String) {
        val targetPath = resolvePath(outputDir, templatePath)
        if (Files.exists(targetPath)) {
            logD(TAG, "Template already exists, skipping: $targetPath")
            return
        }
        val resourceStream = javaClass.classLoader.getResourceAsStream(templatePath)
        if (resourceStream != null) {
            resourceStream.use { stream ->
                Files.createDirectories(targetPath.parent)
                Files.copy(stream, targetPath)
                logI(TAG, "Copied template from resources: $templatePath -> $targetPath")
            }
        } else {
            logW(TAG, "Template not found in resources: $templatePath")
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

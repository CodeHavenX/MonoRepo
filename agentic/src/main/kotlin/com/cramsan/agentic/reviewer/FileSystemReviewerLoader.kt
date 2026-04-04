package com.cramsan.agentic.reviewer

import com.cramsan.agentic.core.ReviewerDefinition
import com.cramsan.framework.logging.logI
import java.nio.file.Files
import java.nio.file.Path

private const val TAG = "FileSystemReviewerLoader"

class FileSystemReviewerLoader(private val reviewersDir: Path) : ReviewerLoader {

    override fun loadAll(): List<ReviewerDefinition> {
        if (!Files.isDirectory(reviewersDir)) return emptyList()

        val definitions = mutableListOf<ReviewerDefinition>()
        Files.list(reviewersDir).use { stream ->
            stream
                .filter { it.toString().endsWith(".md") }
                .forEach { path ->
                    val name = path.fileName.toString().removeSuffix(".md")
                    val systemPrompt = Files.readString(path)
                    definitions.add(ReviewerDefinition(name = name, systemPrompt = systemPrompt))
                }
        }

        logI(TAG, "Loaded ${definitions.size} reviewer definitions from $reviewersDir")
        return definitions
    }
}

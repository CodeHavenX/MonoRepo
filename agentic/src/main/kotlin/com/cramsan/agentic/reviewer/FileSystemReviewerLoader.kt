package com.cramsan.agentic.reviewer

import com.cramsan.agentic.core.ReviewerDefinition
import com.cramsan.framework.logging.logD
import com.cramsan.framework.logging.logI
import com.cramsan.framework.logging.logW
import java.nio.file.Files
import java.nio.file.Path

private const val TAG = "FileSystemReviewerLoader"

class FileSystemReviewerLoader(private val reviewersDir: Path) : ReviewerLoader {

    override fun loadAll(): List<ReviewerDefinition> {
        logD(TAG, "loadAll called: reviewersDir=$reviewersDir")

        if (!Files.isDirectory(reviewersDir)) {
            logW(TAG, "Reviewers directory does not exist or is not a directory: $reviewersDir; returning empty list")
            return emptyList()
        }

        val definitions = mutableListOf<ReviewerDefinition>()
        Files.list(reviewersDir).use { stream ->
            stream
                .filter { it.toString().endsWith(".md") }
                .forEach { path ->
                    logD(TAG, "Found reviewer file: $path")
                    val name = path.fileName.toString().removeSuffix(".md")
                    val systemPrompt = Files.readString(path)
                    logD(TAG, "Loaded reviewer '$name' (promptLength=${systemPrompt.length} chars)")
                    definitions.add(ReviewerDefinition(name = name, systemPrompt = systemPrompt))
                }
        }

        logI(TAG, "Loaded ${definitions.size} reviewer definition(s) from $reviewersDir")
        return definitions
    }
}

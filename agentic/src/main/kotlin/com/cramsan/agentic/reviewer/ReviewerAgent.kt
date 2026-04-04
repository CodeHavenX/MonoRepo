package com.cramsan.agentic.reviewer

import com.cramsan.agentic.core.AgenticDocument
import com.cramsan.agentic.core.ReviewerDefinition
import com.cramsan.agentic.core.ReviewerFeedback
import com.cramsan.agentic.core.Task

interface ReviewerAgent {
    suspend fun reviewDocuments(
        reviewer: ReviewerDefinition,
        documents: List<AgenticDocument>,
    ): ReviewerFeedback

    suspend fun reviewCode(
        reviewer: ReviewerDefinition,
        task: Task,
        diff: String,
    ): ReviewerFeedback
}

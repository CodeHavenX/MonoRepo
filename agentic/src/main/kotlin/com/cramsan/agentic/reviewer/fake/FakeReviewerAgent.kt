package com.cramsan.agentic.reviewer.fake

import com.cramsan.agentic.core.AgenticDocument
import com.cramsan.agentic.core.ReviewerDefinition
import com.cramsan.agentic.core.ReviewerFeedback
import com.cramsan.agentic.core.Task
import com.cramsan.agentic.reviewer.ReviewerAgent

class FakeReviewerAgent(
    private val documentFeedback: String = "No issues found.",
    private val codeFeedback: String = "LGTM.",
) : ReviewerAgent {

    override suspend fun reviewDocuments(
        reviewer: ReviewerDefinition,
        documents: List<AgenticDocument>,
    ): ReviewerFeedback {
        return ReviewerFeedback(reviewerName = reviewer.name, content = documentFeedback)
    }

    override suspend fun reviewCode(
        reviewer: ReviewerDefinition,
        task: Task,
        diff: String,
    ): ReviewerFeedback {
        return ReviewerFeedback(reviewerName = reviewer.name, content = codeFeedback)
    }
}

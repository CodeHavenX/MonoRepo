package com.cramsan.agentic.reviewer.fake

import com.cramsan.agentic.core.AgenticDocument
import com.cramsan.agentic.core.ReviewerDefinition
import com.cramsan.agentic.core.ReviewerFeedback
import com.cramsan.agentic.core.Task
import com.cramsan.agentic.reviewer.ReviewerAgent
import com.cramsan.framework.logging.logD

private const val TAG = "FakeReviewerAgent"

/**
 * In-memory [com.cramsan.agentic.reviewer.ReviewerAgent] for tests. Returns [documentFeedback]
 * for all [reviewDocuments] calls and [codeFeedback] for all [reviewCode] calls, regardless of
 * the input content. The [com.cramsan.agentic.core.ReviewerDefinition.name] from the reviewer
 * definition is echoed back as [com.cramsan.agentic.core.ReviewerFeedback.reviewerName].
 */
class FakeReviewerAgent(
    private val documentFeedback: String = "No issues found.",
    private val codeFeedback: String = "LGTM.",
) : ReviewerAgent {

    override suspend fun reviewDocuments(
        reviewer: ReviewerDefinition,
        documents: List<AgenticDocument>,
    ): ReviewerFeedback {
        logD(TAG, "reviewDocuments called: reviewer='${reviewer.name}', documentCount=${documents.size}")
        logD(TAG, "Returning canned document feedback: '$documentFeedback'")
        return ReviewerFeedback(reviewerName = reviewer.name, content = documentFeedback)
    }

    override suspend fun reviewCode(
        reviewer: ReviewerDefinition,
        task: Task,
        diff: String,
    ): ReviewerFeedback {
        logD(TAG, "reviewCode called: reviewer='${reviewer.name}', taskId=${task.id}, diffLength=${diff.length}")
        logD(TAG, "Returning canned code feedback: '$codeFeedback'")
        return ReviewerFeedback(reviewerName = reviewer.name, content = codeFeedback)
    }
}

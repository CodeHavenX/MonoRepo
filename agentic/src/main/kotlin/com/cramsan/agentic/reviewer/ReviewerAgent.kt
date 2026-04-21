package com.cramsan.agentic.reviewer

import com.cramsan.agentic.core.AgenticDocument
import com.cramsan.agentic.core.ReviewerDefinition
import com.cramsan.agentic.core.ReviewerFeedback
import com.cramsan.agentic.core.Task

/**
 * Performs AI-powered review of planning documents and agent-produced code changes.
 *
 * Reviewer agents are stateless and isolated: each invocation sends only the reviewer's system
 * prompt plus the artifact being reviewed — no prior conversation history. This ensures
 * reviewers give independent, unbiased feedback.
 *
 * **Two usage contexts**:
 * 1. **Document review** (planning phase): [reviewDocuments] is called by
 *    [com.cramsan.agentic.input.DefaultValidationService] in parallel for each reviewer after the
 *    main AI validation pass. Feedback is printed to the console.
 * 2. **Code review** (execution phase): [reviewCode] is called by
 *    [com.cramsan.agentic.execution.DefaultAgentRunner] after a PR is opened. Feedback is posted
 *    as a PR comment.
 *
 * Implementations:
 * - [com.cramsan.agentic.reviewer.claude.ClaudeReviewerAgent]: delegates to [com.cramsan.agentic.ai.AiProvider]
 * - [com.cramsan.agentic.reviewer.fake.FakeReviewerAgent]: returns canned responses for testing
 */
interface ReviewerAgent {
    /**
     * Reviews [documents] using the [reviewer]'s system prompt. Currently sends only document
     * metadata (id, path, status), not the actual document content.
     * // TODO: load and include document content in the review prompt for meaningful feedback.
     */
    suspend fun reviewDocuments(
        reviewer: ReviewerDefinition,
        documents: List<AgenticDocument>,
    ): ReviewerFeedback

    /**
     * Reviews code changes for [task] using [reviewer]'s system prompt. [diff] is expected to
     * be a git-format patch or diff; however, in the current implementation it is only the PR
     * title — not a real diff. See [com.cramsan.agentic.execution.DefaultAgentRunner].
     */
    suspend fun reviewCode(
        reviewer: ReviewerDefinition,
        task: Task,
        diff: String,
    ): ReviewerFeedback
}

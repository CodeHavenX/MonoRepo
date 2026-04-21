package com.cramsan.agentic.reviewer

import com.cramsan.agentic.core.ReviewerDefinition

/**
 * Loads [com.cramsan.agentic.core.ReviewerDefinition]s from a configured source.
 * Called on each use (not cached), so changes to reviewer files take effect on the next
 * invocation without restarting the process.
 *
 * Implementations:
 * - [com.cramsan.agentic.reviewer.FileSystemReviewerLoader]: reads `.md` files from a directory.
 * - [com.cramsan.agentic.reviewer.ConfigurableReviewerLoader]: combines embedded defaults with
 *   optional user-provided overrides from the filesystem.
 */
interface ReviewerLoader {
    /** Returns all available reviewer definitions. Returns an empty list if none are configured. */
    fun loadAll(): List<ReviewerDefinition>
}

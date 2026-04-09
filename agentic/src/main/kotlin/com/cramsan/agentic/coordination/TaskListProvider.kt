package com.cramsan.agentic.coordination

import com.cramsan.agentic.core.Task

/**
 * Abstraction for providing the list of tasks to execute.
 *
 * On first use, implementations may parse tasks from a source document and
 * persist them as individual task tracking files. On subsequent calls,
 * implementations may read from the persisted files instead.
 */
interface TaskListProvider {

    /**
     * Returns the full list of tasks.
     *
     * If the provider has not been initialized yet, this call will also
     * persist individual task tracking files to disk as a side effect.
     *
     * This method is idempotent: calling it multiple times is safe.
     */
    suspend fun provide(): List<Task>

    /**
     * Returns true if task tracking files have already been written to disk.
     */
    fun isInitialized(): Boolean
}

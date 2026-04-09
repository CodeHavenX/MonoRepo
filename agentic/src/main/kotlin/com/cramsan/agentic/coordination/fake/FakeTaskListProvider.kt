package com.cramsan.agentic.coordination.fake

import com.cramsan.agentic.coordination.TaskListProvider
import com.cramsan.agentic.core.Task

/**
 * A [TaskListProvider] implementation for testing.
 * Returns a fixed list of tasks without any filesystem side effects.
 */
class FakeTaskListProvider(
    private val tasks: List<Task> = emptyList(),
    private var initialized: Boolean = false,
) : TaskListProvider {

    val providedTasks: MutableList<List<Task>> = mutableListOf()

    override suspend fun provide(): List<Task> {
        initialized = true
        providedTasks.add(tasks)
        return tasks
    }

    override fun isInitialized(): Boolean = initialized
}

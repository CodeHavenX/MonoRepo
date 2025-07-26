package com.cramsan.sample.frontend.shared.domain.repository

import com.cramsan.sample.frontend.shared.domain.entities.Task
import com.cramsan.sample.frontend.shared.domain.entities.TaskId
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface defining the contract for task data operations.
 * This follows the dependency inversion principle - the domain layer
 * defines the interface but doesn't depend on the implementation.
 */
interface TaskRepository {

    /**
     * Get all tasks as a Flow for reactive updates
     */
    fun getAllTasks(): Flow<List<Task>>

    /**
     * Get a specific task by ID
     */
    suspend fun getTaskById(id: TaskId): Task?

    /**
     * Add a new task
     */
    suspend fun addTask(task: Task): Result<Unit>

    /**
     * Update an existing task
     */
    suspend fun updateTask(task: Task): Result<Unit>

    /**
     * Delete a task by ID
     */
    suspend fun deleteTask(id: TaskId): Result<Unit>

    /**
     * Get tasks filtered by completion status
     */
    fun getTasksByCompletionStatus(completed: Boolean): Flow<List<Task>>

    /**
     * Search tasks by title or description
     */
    suspend fun searchTasks(query: String): List<Task>
}

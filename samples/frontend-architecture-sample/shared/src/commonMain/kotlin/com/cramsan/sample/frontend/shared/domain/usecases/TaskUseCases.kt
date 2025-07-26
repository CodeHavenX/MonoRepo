package com.cramsan.sample.frontend.shared.domain.usecases

import com.cramsan.sample.frontend.shared.domain.entities.Task
import com.cramsan.sample.frontend.shared.domain.entities.TaskId
import com.cramsan.sample.frontend.shared.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Use case for getting all tasks.
 * Encapsulates the business logic for retrieving and sorting tasks.
 */
class GetAllTasksUseCase(
    private val taskRepository: TaskRepository
) {
    /**
     * Get all tasks sorted by priority (highest first) and then by creation date
     * @return Flow of sorted tasks
     */
    operator fun invoke(): Flow<List<Task>> {
        return taskRepository.getAllTasks().map { tasks ->
            tasks.sortedWith(
                compareByDescending<Task> { it.priority.sortOrder }
                    .thenBy { it.createdAt }
            )
        }
    }
}

/**
 * Use case for toggling task completion status.
 * Implements business rules for task completion.
 */
class ToggleTaskCompletionUseCase(
    private val taskRepository: TaskRepository
) {
    /**
     * Toggle the completion status of a task
     * @param taskId The ID of the task to toggle
     * @return Result indicating success or failure with appropriate error
     */
    suspend operator fun invoke(taskId: TaskId): Result<Unit> {
        val task = taskRepository.getTaskById(taskId)
            ?: return Result.failure(TaskNotFoundException(taskId))

        // Apply business rule: check if task can be completed
        if (!task.isCompleted && !task.canBeCompleted()) {
            return Result.failure(TaskCannotBeCompletedException(task))
        }

        val updatedTask = if (task.isCompleted) {
            // Mark as incomplete
            task.copy(
                isCompleted = false,
                completedAt = null
            )
        } else {
            // Mark as complete
            task.copy(
                isCompleted = true,
                completedAt = kotlinx.datetime.Clock.System.now()
            )
        }

        return taskRepository.updateTask(updatedTask)
    }
}

/**
 * Use case for creating a new task.
 * Handles validation and ID generation.
 */
class CreateTaskUseCase(
    private val taskRepository: TaskRepository
) {
    /**
     * Create a new task with the given parameters
     * @param title The task title (required, non-blank)
     * @param description The task description
     * @param priority The task priority level
     * @param dueDate Optional due date for the task
     * @return Result containing the created task or an error
     */
    suspend operator fun invoke(
        title: String,
        description: String,
        priority: com.cramsan.sample.frontend.shared.domain.entities.TaskPriority,
        dueDate: kotlinx.datetime.Instant? = null
    ): Result<Task> {
        // Validate input
        if (title.isBlank()) {
            return Result.failure(InvalidTaskDataException("Title cannot be empty"))
        }

        val task = Task(
            id = TaskId(generateTaskId()),
            title = title.trim(),
            description = description.trim(),
            priority = priority,
            createdAt = kotlinx.datetime.Clock.System.now(),
            dueDate = dueDate
        )

        return taskRepository.addTask(task).map { task }
    }

    private fun generateTaskId(): String {
        // Simple ID generation - in a real app, this might use UUID or database sequence
        return "task_${kotlinx.datetime.Clock.System.now().toEpochMilliseconds()}"
    }
}

/**
 * Exception thrown when a task is not found
 */
class TaskNotFoundException(val taskId: TaskId) : Exception("Task with ID ${taskId.value} not found")

/**
 * Exception thrown when a task cannot be completed due to business rules
 */
class TaskCannotBeCompletedException(val task: Task) : Exception("Task '${task.title}' cannot be completed")

/**
 * Exception thrown when task data is invalid
 */
class InvalidTaskDataException(message: String) : Exception(message)

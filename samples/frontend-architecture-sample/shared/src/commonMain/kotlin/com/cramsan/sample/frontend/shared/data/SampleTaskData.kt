package com.cramsan.sample.frontend.shared.data

import com.cramsan.sample.frontend.shared.domain.entities.Task
import com.cramsan.sample.frontend.shared.domain.entities.TaskId
import com.cramsan.sample.frontend.shared.domain.entities.TaskPriority
import com.cramsan.sample.frontend.shared.domain.repository.TaskRepository
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

/**
 * Sample data provider for the task management application.
 * Provides initial tasks for demonstration purposes.
 */
object SampleTaskData {

    /**
     * Create a list of sample tasks for demonstration purposes
     * @return List of sample tasks with various priorities and states
     */
    fun createSampleTasks(): List<Task> {
        val now = Clock.System.now()

        return listOf(
            Task(
                id = TaskId("sample_1"),
                title = "Complete project proposal",
                description = "Write and submit the Q4 project proposal document " +
                    "including budget estimates and timeline.",
                priority = TaskPriority.HIGH,
                createdAt = now - 2.days,
                dueDate = now + 3.days
            ),
            Task(
                id = TaskId("sample_2"),
                title = "Review code changes",
                description = "Review pull requests for the authentication module.",
                priority = TaskPriority.MEDIUM,
                createdAt = now - 1.days,
                isCompleted = true,
                completedAt = now - 4.hours
            ),
            Task(
                id = TaskId("sample_3"),
                title = "Update documentation",
                description = "Update the API documentation to reflect recent changes " +
                    "in the user authentication endpoints.",
                priority = TaskPriority.LOW,
                createdAt = now - 3.days
            ),
            Task(
                id = TaskId("sample_4"),
                title = "Fix critical bug",
                description = "Resolve the memory leak issue reported in production environment.",
                priority = TaskPriority.URGENT,
                createdAt = now - 6.hours,
                dueDate = now + 6.hours
            ),
            Task(
                id = TaskId("sample_5"),
                title = "Team meeting preparation",
                description = "Prepare agenda and materials for the weekly team sync meeting.",
                priority = TaskPriority.MEDIUM,
                createdAt = now - 12.hours,
                dueDate = now + 1.days
            )
        )
    }

    /**
     * Initialize repository with sample data
     */
    suspend fun initializeWithSampleData(repository: TaskRepository) {
        createSampleTasks().forEach { task ->
            repository.addTask(task)
        }
    }
}

package com.cramsan.edifikana.server.datastore

import com.cramsan.edifikana.lib.model.CommonAreaId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.TaskId
import com.cramsan.edifikana.lib.model.TaskPriority
import com.cramsan.edifikana.lib.model.TaskStatus
import com.cramsan.edifikana.lib.model.UnitId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.service.models.Task
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Interface for the task datastore.
 */
@OptIn(ExperimentalTime::class)
interface TaskDatastore {

    /**
     * Creates a new task record scoped to [propertyId]. Returns the created [Task].
     */
    suspend fun createTask(
        propertyId: PropertyId,
        unitId: UnitId?,
        commonAreaId: CommonAreaId?,
        assigneeId: UserId?,
        createdBy: UserId,
        title: String,
        description: String?,
        priority: TaskPriority,
        dueDate: Instant?,
    ): Result<Task>

    /**
     * Retrieves a task by [taskId]. Returns null if not found or soft-deleted.
     */
    suspend fun getTask(taskId: TaskId): Result<Task?>

    /**
     * Retrieves all non-deleted tasks for the given [propertyId], with optional filters.
     */
    suspend fun getTasks(
        propertyId: PropertyId,
        unitId: UnitId? = null,
        status: TaskStatus? = null,
        assigneeId: UserId? = null,
        priority: TaskPriority? = null,
    ): Result<List<Task>>

    /**
     * Updates an existing task. Only non-null parameters are applied.
     * Audit fields ([statusChangedBy], [completedAt], [statusChangedAt]) are managed
     * by the service layer and passed through here.
     */
    suspend fun updateTask(
        taskId: TaskId,
        title: String?,
        description: String?,
        priority: TaskPriority?,
        status: TaskStatus?,
        assigneeId: UserId?,
        dueDate: Instant?,
        statusChangedBy: UserId?,
        completedAt: Instant?,
        statusChangedAt: Instant?,
    ): Result<Task>

    /**
     * Soft-deletes the task with the given [taskId]. Returns true if the record was deleted.
     */
    suspend fun deleteTask(taskId: TaskId): Result<Boolean>

    /**
     * Hard-deletes the task with the given [taskId]. For integration test cleanup only.
     *
     * The returned [Result] will be:
     * - `Result.success(true)` if a matching record existed and was hard-deleted.
     * - `Result.success(false)` if no matching record exists.
     */
    suspend fun purgeTask(taskId: TaskId): Result<Boolean>
}

package com.cramsan.edifikana.server.service

import com.cramsan.edifikana.lib.model.CommonAreaId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.TaskId
import com.cramsan.edifikana.lib.model.TaskPriority
import com.cramsan.edifikana.lib.model.TaskStatus
import com.cramsan.edifikana.lib.model.UnitId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.datastore.TaskDatastore
import com.cramsan.edifikana.server.service.models.Task
import com.cramsan.framework.logging.logD
import com.cramsan.framework.utils.exceptions.ClientRequestExceptions.InvalidRequestException
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Service for managing tasks. Delegates persistence to [TaskDatastore] and enforces
 * status-transition rules before delegating updates.
 */
@OptIn(ExperimentalTime::class)
class TaskService(
    private val taskDatastore: TaskDatastore,
    private val clock: Clock,
) {

    /**
     * Creates a new task scoped to [propertyId]. At least one of [unitId] or [commonAreaId]
     * must be provided to locate the task within the property. [createdBy] is the authenticated
     * caller's user ID.
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
    ): Task {
        logD(TAG, "createTask")
        if (unitId == null && commonAreaId == null) {
            throw InvalidRequestException("A task must be associated with either a unit or a common area.")
        }
        return taskDatastore.createTask(
            propertyId = propertyId,
            unitId = unitId,
            commonAreaId = commonAreaId,
            assigneeId = assigneeId,
            createdBy = createdBy,
            title = title,
            description = description,
            priority = priority,
            dueDate = dueDate,
        ).getOrThrow()
    }

    /**
     * Retrieves a single task by [taskId]. Returns null if not found or soft-deleted.
     */
    suspend fun getTask(taskId: TaskId): Task? {
        logD(TAG, "getTask")
        return taskDatastore.getTask(taskId).getOrNull()
    }

    /**
     * Lists all non-deleted tasks for the given [propertyId], with optional filters.
     */
    suspend fun getTasks(
        propertyId: PropertyId,
        unitId: UnitId?,
        status: TaskStatus?,
        assigneeId: UserId?,
        priority: TaskPriority?,
    ): List<Task> {
        logD(TAG, "getTasks")
        return taskDatastore.getTasks(
            propertyId = propertyId,
            unitId = unitId,
            status = status,
            assigneeId = assigneeId,
            priority = priority,
        ).getOrThrow()
    }

    /**
     * Updates an existing task. Validates status transitions and sets audit fields when
     * the status changes. [callerUserId] is the authenticated caller's user ID.
     */
    suspend fun updateTask(
        taskId: TaskId,
        title: String?,
        description: String?,
        priority: TaskPriority?,
        status: TaskStatus?,
        assigneeId: UserId?,
        dueDate: Instant?,
        callerUserId: UserId,
    ): Task {
        logD(TAG, "updateTask")
        val current = taskDatastore.getTask(taskId).getOrThrow()
            ?: throw InvalidRequestException("Task not found: $taskId")

        var completedAt: Instant? = null
        var statusChangedAt: Instant? = null
        var statusChangedBy: UserId? = null

        if (status != null && status != current.status) {
            validateStatusTransition(current.status, status)
            val now = clock.now()
            statusChangedAt = now
            statusChangedBy = callerUserId
            if (status == TaskStatus.COMPLETED) {
                completedAt = now
            }
        }

        return taskDatastore.updateTask(
            taskId = taskId,
            title = title,
            description = description,
            priority = priority,
            status = status,
            assigneeId = assigneeId,
            dueDate = dueDate,
            statusChangedBy = statusChangedBy,
            completedAt = completedAt,
            statusChangedAt = statusChangedAt,
        ).getOrThrow()
    }

    /**
     * Soft-deletes a task. Returns true if successfully deleted.
     */
    suspend fun deleteTask(taskId: TaskId): Boolean {
        logD(TAG, "deleteTask")
        return taskDatastore.deleteTask(taskId).getOrThrow()
    }

    /**
     * Validates that transitioning from [current] to [next] is permitted.
     *
     * Allowed transitions:
     * - OPEN → IN_PROGRESS, COMPLETED, CANCELLED
     * - IN_PROGRESS → OPEN, COMPLETED, CANCELLED
     * - COMPLETED → (terminal)
     * - CANCELLED → (terminal)
     */
    private fun validateStatusTransition(current: TaskStatus, next: TaskStatus) {
        val allowed = when (current) {
            TaskStatus.OPEN -> setOf(TaskStatus.IN_PROGRESS, TaskStatus.COMPLETED, TaskStatus.CANCELLED)
            TaskStatus.IN_PROGRESS -> setOf(TaskStatus.OPEN, TaskStatus.COMPLETED, TaskStatus.CANCELLED)
            TaskStatus.COMPLETED -> emptySet()
            TaskStatus.CANCELLED -> emptySet()
        }
        if (next !in allowed) {
            throw InvalidRequestException(
                "Invalid status transition: cannot move from $current to $next"
            )
        }
    }

    companion object {
        private const val TAG = "TaskService"
    }
}

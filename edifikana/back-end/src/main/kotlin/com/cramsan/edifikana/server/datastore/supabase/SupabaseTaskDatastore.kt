package com.cramsan.edifikana.server.datastore.supabase

import com.cramsan.edifikana.lib.model.commonArea.CommonAreaId
import com.cramsan.edifikana.lib.model.property.PropertyId
import com.cramsan.edifikana.lib.model.task.TaskId
import com.cramsan.edifikana.lib.model.task.TaskPriority
import com.cramsan.edifikana.lib.model.task.TaskStatus
import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.edifikana.lib.model.user.UserId
import com.cramsan.edifikana.server.datastore.TaskDatastore
import com.cramsan.edifikana.server.datastore.supabase.models.TaskEntity
import com.cramsan.edifikana.server.datastore.supabase.models.TaskEntity.CreateTaskEntity
import com.cramsan.edifikana.server.service.models.Task
import com.cramsan.framework.annotations.SupabaseModel
import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.framework.logging.logD
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.datetime.LocalDate
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Supabase implementation of [TaskDatastore].
 */
@OptIn(ExperimentalTime::class)
class SupabaseTaskDatastore(
    private val postgrest: Postgrest,
    private val clock: Clock,
) : TaskDatastore {

    /**
     * Inserts a new task row and returns the created [Task].
     */
    @OptIn(SupabaseModel::class)
    override suspend fun createTask(
        propertyId: PropertyId,
        unitId: UnitId?,
        commonAreaId: CommonAreaId?,
        assigneeId: UserId?,
        createdBy: UserId,
        title: String,
        description: String?,
        priority: TaskPriority,
        dueDate: LocalDate?,
    ): Result<Task> = runSuspendCatching(TAG) {
        logD(TAG, "Creating task: %s", title)
        val entity = CreateTaskEntity(
            propertyId = propertyId,
            unitId = unitId,
            commonAreaId = commonAreaId,
            assigneeId = assigneeId,
            createdBy = createdBy,
            title = title,
            description = description,
            priority = priority.name,
            dueDate = dueDate,
        )
        postgrest.from(TaskEntity.COLLECTION).insert(entity) {
            select()
        }.decodeSingle<TaskEntity>().toTask()
    }

    /**
     * Retrieves a single task by [taskId]. Returns null if not found or soft-deleted.
     */
    @OptIn(SupabaseModel::class)
    override suspend fun getTask(taskId: TaskId): Result<Task?> = runSuspendCatching(TAG) {
        logD(TAG, "Getting task: %s", taskId)
        postgrest.from(TaskEntity.COLLECTION).select {
            filter {
                TaskEntity::id eq taskId.taskId
                TaskEntity::deletedAt isExact null
            }
        }.decodeSingleOrNull<TaskEntity>()?.toTask()
    }

    /**
     * Lists all non-deleted tasks for the given [propertyId], with optional filters.
     */
    @OptIn(SupabaseModel::class)
    override suspend fun getTasks(
        propertyId: PropertyId,
        unitId: UnitId?,
        status: TaskStatus?,
        assigneeId: UserId?,
        priority: TaskPriority?,
    ): Result<List<Task>> = runSuspendCatching(TAG) {
        logD(TAG, "Getting tasks for property: %s", propertyId)
        postgrest.from(TaskEntity.COLLECTION).select {
            filter {
                TaskEntity::propertyId eq propertyId.propertyId
                TaskEntity::deletedAt isExact null
                unitId?.let { TaskEntity::unitId eq it.unitId }
                status?.let { TaskEntity::status eq it.name }
                assigneeId?.let { TaskEntity::assigneeId eq it.userId }
                priority?.let { TaskEntity::priority eq it.name }
            }
        }.decodeList<TaskEntity>().map { it.toTask() }
    }

    /**
     * Updates an existing task. Only non-null parameters are applied.
     */
    @OptIn(SupabaseModel::class)
    override suspend fun updateTask(
        taskId: TaskId,
        title: String?,
        description: String?,
        priority: TaskPriority?,
        status: TaskStatus?,
        assigneeId: UserId?,
        dueDate: LocalDate?,
        statusChangedBy: UserId?,
        completedAt: Instant?,
        statusChangedAt: Instant?,
    ): Result<Task> = runSuspendCatching(TAG) {
        logD(TAG, "Updating task: %s", taskId)
        postgrest.from(TaskEntity.COLLECTION).update({
            title?.let { value -> TaskEntity::title setTo value }
            description?.let { value -> TaskEntity::description setTo value }
            priority?.let { value -> TaskEntity::priority setTo value.name }
            status?.let { value -> TaskEntity::status setTo value.name }
            assigneeId?.let { value -> TaskEntity::assigneeId setTo value }
            dueDate?.let { value -> TaskEntity::dueDate setTo value }
            statusChangedBy?.let { value -> TaskEntity::statusChangedBy setTo value }
            completedAt?.let { value -> TaskEntity::completedAt setTo value }
            statusChangedAt?.let { value -> TaskEntity::statusChangedAt setTo value }
        }) {
            select()
            filter {
                TaskEntity::id eq taskId.taskId
                TaskEntity::deletedAt isExact null
            }
        }.decodeSingle<TaskEntity>().toTask()
    }

    /**
     * Soft-deletes a task by setting [TaskEntity.deletedAt]. Returns true if the record was found and deleted.
     */
    @OptIn(SupabaseModel::class)
    override suspend fun deleteTask(taskId: TaskId): Result<Boolean> = runSuspendCatching(TAG) {
        logD(TAG, "Soft deleting task: %s", taskId)
        postgrest.from(TaskEntity.COLLECTION).update({
            TaskEntity::deletedAt setTo clock.now()
        }) {
            select()
            filter {
                TaskEntity::id eq taskId.taskId
                TaskEntity::deletedAt isExact null
            }
        }.decodeSingleOrNull<TaskEntity>() != null
    }

    /**
     * Hard-deletes a task row. For integration test cleanup only.
     */
    @OptIn(SupabaseModel::class)
    override suspend fun purgeTask(taskId: TaskId): Result<Boolean> = runSuspendCatching(TAG) {
        logD(TAG, "Purging task: %s", taskId)
        postgrest.from(TaskEntity.COLLECTION).delete {
            select()
            filter {
                TaskEntity::id eq taskId.taskId
            }
        }.decodeSingleOrNull<TaskEntity>() != null
    }

    companion object {
        const val TAG = "SupabaseTaskDatastore"
    }
}

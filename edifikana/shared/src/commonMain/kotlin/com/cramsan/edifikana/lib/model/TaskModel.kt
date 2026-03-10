package com.cramsan.edifikana.lib.model

/**
 * Domain model representing a task.
 *
 * Timestamp fields are represented as epoch seconds (Long) to avoid exposing
 * the experimental [kotlin.time.Instant] type in the public API.
 */
data class TaskModel(
    val id: TaskId,
    val orgId: OrganizationId,
    val propertyId: PropertyId?,
    val unitId: UnitId?,
    val commonAreaId: CommonAreaId?,
    val assigneeId: UserId?,
    val createdBy: UserId,
    val statusChangedBy: UserId?,
    val title: String,
    val description: String?,
    val priority: TaskPriority,
    val status: TaskStatus,
    val dueDate: Long?,
    val createdAt: Long,
    val completedAt: Long?,
    val statusChangedAt: Long?,
)

package com.cramsan.edifikana.lib.model

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Domain model representing a task.
 */
@OptIn(ExperimentalTime::class)
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
    val dueDate: Instant?,
    val createdAt: Instant,
    val completedAt: Instant?,
    val statusChangedAt: Instant?,
)

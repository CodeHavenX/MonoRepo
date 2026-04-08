package com.cramsan.edifikana.lib.model

import kotlinx.datetime.LocalDate
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Domain model representing a task.
 *
 * Tasks are property-scoped: [propertyId] is always present. [unitId] and [commonAreaId]
 * are optional sub-scoping fields within the property.
 */
@OptIn(ExperimentalTime::class)
data class TaskModel(
    val id: TaskId,
    val propertyId: PropertyId,
    val unitId: UnitId?,
    val commonAreaId: CommonAreaId?,
    val assigneeId: UserId?,
    val createdBy: UserId,
    val statusChangedBy: UserId?,
    val title: String,
    val description: String?,
    val priority: TaskPriority,
    val status: TaskStatus,
    val dueDate: LocalDate?,
    val createdAt: Instant,
    val completedAt: Instant?,
    val statusChangedAt: Instant?,
)

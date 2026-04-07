package com.cramsan.edifikana.server.service.models

import com.cramsan.edifikana.lib.model.CommonAreaId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.TaskId
import com.cramsan.edifikana.lib.model.TaskPriority
import com.cramsan.edifikana.lib.model.TaskStatus
import com.cramsan.edifikana.lib.model.UnitId
import com.cramsan.edifikana.lib.model.UserId
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Domain model representing a task within a property.
 *
 * Tasks are property-scoped: [propertyId] is always present. [unitId] and [commonAreaId]
 * are optional sub-scoping fields within the property.
 */
@OptIn(ExperimentalTime::class)
data class Task(
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
    val dueDate: Instant?,
    val createdAt: Instant,
    val completedAt: Instant?,
    val statusChangedAt: Instant?,
)

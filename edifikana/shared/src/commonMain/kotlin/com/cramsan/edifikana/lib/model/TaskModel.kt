package com.cramsan.edifikana.lib.model

import com.cramsan.edifikana.lib.model.commonArea.CommonAreaId
import com.cramsan.edifikana.lib.model.property.PropertyId
import com.cramsan.edifikana.lib.model.task.TaskId
import com.cramsan.edifikana.lib.model.task.TaskPriority
import com.cramsan.edifikana.lib.model.task.TaskStatus
import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.edifikana.lib.model.user.UserId
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

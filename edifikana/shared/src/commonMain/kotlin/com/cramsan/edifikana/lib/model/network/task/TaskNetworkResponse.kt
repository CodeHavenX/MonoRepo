package com.cramsan.edifikana.lib.model.network.task

import com.cramsan.edifikana.lib.model.commonArea.CommonAreaId
import com.cramsan.edifikana.lib.model.property.PropertyId
import com.cramsan.edifikana.lib.model.task.TaskId
import com.cramsan.edifikana.lib.model.task.TaskPriority
import com.cramsan.edifikana.lib.model.task.TaskStatus
import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.edifikana.lib.model.user.UserId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Network response for a task.
 *
 * Tasks are property-scoped: [propertyId] is always present. [unitId] and [commonAreaId]
 * are optional sub-scoping fields within the property.
 */
@NetworkModel
@Serializable
@OptIn(ExperimentalTime::class)
data class TaskNetworkResponse(
    val id: TaskId,
    @SerialName("property_id") val propertyId: PropertyId,
    @SerialName("unit_id") val unitId: UnitId?,
    @SerialName("common_area_id") val commonAreaId: CommonAreaId?,
    @SerialName("assignee_id") val assigneeId: UserId?,
    @SerialName("created_by") val createdBy: UserId,
    @SerialName("status_changed_by") val statusChangedBy: UserId?,
    val title: String,
    val description: String?,
    val priority: TaskPriority,
    val status: TaskStatus,
    @SerialName("due_date") val dueDate: LocalDate?,
    @SerialName("created_at") val createdAt: Instant,
    @SerialName("completed_at") val completedAt: Instant?,
    @SerialName("status_changed_at") val statusChangedAt: Instant?,
) : ResponseBody

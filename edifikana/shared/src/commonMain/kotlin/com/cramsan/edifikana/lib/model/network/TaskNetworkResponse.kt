package com.cramsan.edifikana.lib.model.network

import com.cramsan.edifikana.lib.model.CommonAreaId
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.TaskId
import com.cramsan.edifikana.lib.model.TaskPriority
import com.cramsan.edifikana.lib.model.TaskStatus
import com.cramsan.edifikana.lib.model.UnitId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Network response for a task.
 */
@NetworkModel
@Serializable
data class TaskNetworkResponse(
    val id: TaskId,
    @SerialName("org_id") val orgId: OrganizationId,
    @SerialName("property_id") val propertyId: PropertyId?,
    @SerialName("unit_id") val unitId: UnitId?,
    @SerialName("common_area_id") val commonAreaId: CommonAreaId?,
    @SerialName("assignee_id") val assigneeId: UserId?,
    @SerialName("created_by") val createdBy: UserId,
    @SerialName("status_changed_by") val statusChangedBy: UserId?,
    val title: String,
    val description: String?,
    val priority: TaskPriority,
    val status: TaskStatus,
    @SerialName("due_date") val dueDate: Long?,
    @SerialName("created_at") val createdAt: Long,
    @SerialName("completed_at") val completedAt: Long?,
    @SerialName("status_changed_at") val statusChangedAt: Long?,
) : ResponseBody

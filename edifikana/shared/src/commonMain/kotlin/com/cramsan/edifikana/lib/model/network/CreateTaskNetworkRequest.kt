package com.cramsan.edifikana.lib.model.network

import com.cramsan.edifikana.lib.model.CommonAreaId
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.TaskPriority
import com.cramsan.edifikana.lib.model.UnitId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.RequestBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Network request to create a new task.
 */
@NetworkModel
@Serializable
data class CreateTaskNetworkRequest(
    @SerialName("org_id") val orgId: OrganizationId,
    @SerialName("property_id") val propertyId: PropertyId?,
    @SerialName("unit_id") val unitId: UnitId?,
    @SerialName("common_area_id") val commonAreaId: CommonAreaId?,
    @SerialName("assignee_id") val assigneeId: UserId?,
    val title: String,
    val description: String?,
    val priority: TaskPriority,
    @SerialName("due_date") val dueDate: Long?,
) : RequestBody

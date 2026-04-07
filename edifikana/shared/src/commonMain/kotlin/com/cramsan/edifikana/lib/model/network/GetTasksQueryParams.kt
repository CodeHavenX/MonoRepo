package com.cramsan.edifikana.lib.model.network

import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.TaskPriority
import com.cramsan.edifikana.lib.model.TaskStatus
import com.cramsan.edifikana.lib.model.UnitId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.QueryParam
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Query parameters for getting tasks.
 *
 * [propertyId] is required — all tasks are property-scoped. The remaining
 * parameters are optional filters applied server-side.
 */
@NetworkModel
@Serializable
data class GetTasksQueryParams(
    @SerialName("property_id") val propertyId: PropertyId,
    @SerialName("unit_id") val unitId: UnitId? = null,
    val status: TaskStatus? = null,
    @SerialName("assignee_id") val assigneeId: UserId? = null,
    val priority: TaskPriority? = null,
) : QueryParam

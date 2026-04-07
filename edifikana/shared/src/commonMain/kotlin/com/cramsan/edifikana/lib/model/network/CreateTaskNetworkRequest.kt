package com.cramsan.edifikana.lib.model.network

import com.cramsan.edifikana.lib.model.CommonAreaId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.TaskPriority
import com.cramsan.edifikana.lib.model.UnitId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.RequestBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Network request to create a new task.
 *
 * Tasks are property-scoped: [propertyId] is required. [unitId] and [commonAreaId]
 * are optional sub-scoping fields within the property.
 */
@NetworkModel
@Serializable
@OptIn(ExperimentalTime::class)
data class CreateTaskNetworkRequest(
    @SerialName("property_id") val propertyId: PropertyId,
    @SerialName("unit_id") val unitId: UnitId? = null,
    @SerialName("common_area_id") val commonAreaId: CommonAreaId? = null,
    @SerialName("assignee_id") val assigneeId: UserId? = null,
    val title: String,
    val description: String? = null,
    val priority: TaskPriority,
    @SerialName("due_date") val dueDate: Instant? = null,
) : RequestBody

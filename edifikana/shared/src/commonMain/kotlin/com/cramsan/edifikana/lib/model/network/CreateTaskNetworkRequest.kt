package com.cramsan.edifikana.lib.model.network


import com.cramsan.edifikana.lib.model.commonArea.CommonAreaId
import com.cramsan.edifikana.lib.model.property.PropertyId
import com.cramsan.edifikana.lib.model.task.TaskPriority
import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.edifikana.lib.model.user.UserId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.RequestBody
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Network request to create a new task.
 *
 * Tasks are property-scoped: [propertyId] is required. Either [unitId] or [commonAreaId]
 * is required for sub-scoping fields within the property.
 */
@NetworkModel
@Serializable
@OptIn(ExperimentalTime::class)
data class CreateTaskNetworkRequest(
    @SerialName("property_id") val propertyId: PropertyId,
    @SerialName("unit_id") val unitId: UnitId?,
    @SerialName("common_area_id") val commonAreaId: CommonAreaId?,
    @SerialName("assignee_id") val assigneeId: UserId?,
    val title: String,
    val description: String? = null,
    val priority: TaskPriority,
    @SerialName("due_date") val dueDate: LocalDate?,
) : RequestBody

package com.cramsan.edifikana.lib.model.network.task

import com.cramsan.edifikana.lib.model.employee.EmployeeId
import com.cramsan.edifikana.lib.model.property.PropertyId
import com.cramsan.edifikana.lib.model.task.TaskPriority
import com.cramsan.edifikana.lib.model.task.TaskStatus
import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.QueryParam
import io.ktor.openapi.JsonSchema
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
@JsonSchema.Description(
    "Query parameters for listing tasks, requiring a property id and supporting optional filters.",
)
data class GetTasksQueryParams(
    @SerialName("property_id")
    @JsonSchema.Description("Identifier of the property to list tasks for.")
    val propertyId: PropertyId,
    @SerialName("unit_id")
    @JsonSchema.Description("Optional unit identifier to filter tasks by.")
    val unitId: UnitId? = null,
    @JsonSchema.Description("Optional lifecycle status to filter tasks by.")
    val status: TaskStatus? = null,
    @SerialName("assignee_id")
    @JsonSchema.Description("Optional employee identifier to filter tasks by assignee.")
    val assigneeId: EmployeeId? = null,
    @JsonSchema.Description("Optional priority level to filter tasks by.")
    val priority: TaskPriority? = null,
) : QueryParam

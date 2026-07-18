package com.cramsan.edifikana.lib.model.network.task

import com.cramsan.edifikana.lib.model.commonArea.CommonAreaId
import com.cramsan.edifikana.lib.model.employee.EmployeeId
import com.cramsan.edifikana.lib.model.property.PropertyId
import com.cramsan.edifikana.lib.model.task.TaskPriority
import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.RequestBody
import io.ktor.openapi.JsonSchema
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Network request to create a new task.
 *
 * Tasks are property-scoped: [propertyId] is required. Either [unitId] or [commonAreaId]
 * is required for sub-scoping fields within the property.
 */
@NetworkModel
@Serializable
@JsonSchema.Description("Request payload to create a new task within a property.")
data class CreateTaskNetworkRequest(
    @SerialName("property_id")
    @JsonSchema.Description("Identifier of the property the task belongs to.")
    val propertyId: PropertyId,
    @SerialName("unit_id")
    @JsonSchema.Description("Identifier of the unit to sub-scope the task to, or null if not unit-scoped.")
    val unitId: UnitId?,
    @SerialName("common_area_id")
    @JsonSchema.Description(
        "Identifier of the common area to sub-scope the task to, or null if not common-area-scoped.",
    )
    val commonAreaId: CommonAreaId?,
    @SerialName("assignee_id")
    @JsonSchema.Description("Identifier of the employee to assign the task to, or null to leave unassigned.")
    val assigneeId: EmployeeId?,
    @JsonSchema.Description("Title of the task.")
    @JsonSchema.Example("\"Fix leaking faucet\"")
    val title: String,
    @JsonSchema.Description("Description of the task.")
    val description: String?,
    @JsonSchema.Description("Priority level of the task.")
    val priority: TaskPriority,
    @SerialName("due_date")
    @JsonSchema.Description("Date by which the task is due, or null if no due date is set.")
    @JsonSchema.Format("date")
    val dueDate: LocalDate?,
) : RequestBody

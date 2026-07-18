package com.cramsan.edifikana.lib.model.network.task

import com.cramsan.edifikana.lib.model.commonArea.CommonAreaId
import com.cramsan.edifikana.lib.model.employee.EmployeeId
import com.cramsan.edifikana.lib.model.property.PropertyId
import com.cramsan.edifikana.lib.model.task.TaskId
import com.cramsan.edifikana.lib.model.task.TaskPriority
import com.cramsan.edifikana.lib.model.task.TaskStatus
import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.edifikana.lib.model.user.UserId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import io.ktor.openapi.JsonSchema
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
@JsonSchema.Description("A property-scoped task representing a maintenance or operational work item.")
data class TaskNetworkResponse(
    @JsonSchema.Description("Unique identifier of the task.")
    val id: TaskId,
    @SerialName("property_id")
    @JsonSchema.Description("Identifier of the property the task belongs to.")
    val propertyId: PropertyId,
    @SerialName("unit_id")
    @JsonSchema.Description("Identifier of the unit the task is sub-scoped to, or null if not unit-scoped.")
    val unitId: UnitId?,
    @SerialName("common_area_id")
    @JsonSchema.Description(
        "Identifier of the common area the task is sub-scoped to, or null if not common-area-scoped.",
    )
    val commonAreaId: CommonAreaId?,
    @SerialName("assignee_id")
    @JsonSchema.Description("Identifier of the employee assigned to the task, or null if unassigned.")
    val assigneeId: EmployeeId?,
    @SerialName("created_by")
    @JsonSchema.Description("Identifier of the user who created the task.")
    val createdBy: UserId,
    @SerialName("status_changed_by")
    @JsonSchema.Description("Identifier of the user who last changed the task's status, or null if unchanged.")
    val statusChangedBy: UserId?,
    @JsonSchema.Description("Title of the task.")
    @JsonSchema.Example("\"Fix leaking faucet\"")
    val title: String,
    @JsonSchema.Description("Description of the task, or null if none was provided.")
    val description: String?,
    @JsonSchema.Description("Priority level of the task.")
    val priority: TaskPriority,
    @JsonSchema.Description("Current lifecycle status of the task.")
    val status: TaskStatus,
    @SerialName("due_date")
    @JsonSchema.Description("Date by which the task is due, or null if no due date is set.")
    @JsonSchema.Format("date")
    val dueDate: LocalDate?,
    @SerialName("created_at")
    @JsonSchema.Description("ISO-8601 timestamp when the task was created.")
    @JsonSchema.Format("date-time")
    val createdAt: Instant,
    @SerialName("completed_at")
    @JsonSchema.Description("ISO-8601 timestamp when the task was completed, or null if not completed.")
    @JsonSchema.Format("date-time")
    val completedAt: Instant?,
    @SerialName("status_changed_at")
    @JsonSchema.Description("ISO-8601 timestamp when the task's status was last changed, or null if unchanged.")
    @JsonSchema.Format("date-time")
    val statusChangedAt: Instant?,
) : ResponseBody

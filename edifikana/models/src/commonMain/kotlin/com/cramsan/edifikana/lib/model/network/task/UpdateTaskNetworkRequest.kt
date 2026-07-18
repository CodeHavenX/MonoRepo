package com.cramsan.edifikana.lib.model.network.task

import com.cramsan.edifikana.lib.model.employee.EmployeeId
import com.cramsan.edifikana.lib.model.task.TaskPriority
import com.cramsan.edifikana.lib.model.task.TaskStatus
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.RequestBody
import io.ktor.openapi.JsonSchema
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime

/**
 * Network request to update an existing task. Only provided (non-null) fields are updated.
 */
@NetworkModel
@Serializable
@OptIn(ExperimentalTime::class)
@JsonSchema.Description(
    "Request payload to update an existing task. Only provided (non-null) fields are updated.",
)
data class UpdateTaskNetworkRequest(
    @JsonSchema.Description("New title of the task, or null to leave unchanged.")
    val title: String?,
    @JsonSchema.Description("New description of the task, or null to leave unchanged.")
    val description: String?,
    @JsonSchema.Description("New priority level of the task, or null to leave unchanged.")
    val priority: TaskPriority?,
    @JsonSchema.Description("New lifecycle status of the task, or null to leave unchanged.")
    val status: TaskStatus?,
    @SerialName("assignee_id")
    @JsonSchema.Description("New assignee for the task, or null to leave unchanged.")
    val assigneeId: EmployeeId?,
    @SerialName("due_date")
    @JsonSchema.Description("New due date for the task, or null to leave unchanged.")
    @JsonSchema.Format("date")
    val dueDate: LocalDate?,
) : RequestBody

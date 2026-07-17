package com.cramsan.edifikana.lib.model.network.eventLog

import com.cramsan.edifikana.lib.model.employee.EmployeeId
import com.cramsan.edifikana.lib.model.eventLog.EventLogEventType
import com.cramsan.edifikana.lib.model.property.PropertyId
import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.RequestBody
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

/**
 * Request to create a new event log entry.
 */
@NetworkModel
@Serializable
@JsonSchema.Description("Request payload to create a new event log entry for a property.")
data class CreateEventLogEntryNetworkRequest(
    @SerialName("employee_id")
    @JsonSchema.Description("Identifier of the employee logging the entry.")
    val employeeId: EmployeeId?,
    @SerialName("fallback_employee_name")
    @JsonSchema.Description("Freeform employee name, used when the employee has no registered account.")
    val fallbackEmployeeName: String?,
    @SerialName("property_id")
    @JsonSchema.Description("Identifier of the property the entry belongs to.")
    val propertyId: PropertyId,
    @JsonSchema.Description("Type of the event log entry.")
    val type: EventLogEventType,
    @SerialName("fallback_type")
    @JsonSchema.Description("Freeform event type, used when [type] is OTHER.")
    val fallbackEventType: String?,
    @JsonSchema.Description("ISO-8601 timestamp when the event occurred.")
    @JsonSchema.Format("date-time")
    val timestamp: Instant,
    @JsonSchema.Description("Title of the event log entry.")
    @JsonSchema.Example("\"Package delivered to front desk\"")
    val title: String,
    @JsonSchema.Description("Description of the event log entry.")
    val description: String?,
    @JsonSchema.Description("Identifier of the unit the entry is associated with.")
    val unit: UnitId?,
) : RequestBody

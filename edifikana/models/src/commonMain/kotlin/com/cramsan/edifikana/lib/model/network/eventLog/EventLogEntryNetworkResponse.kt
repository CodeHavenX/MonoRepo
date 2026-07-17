package com.cramsan.edifikana.lib.model.network.eventLog

import com.cramsan.edifikana.lib.model.employee.EmployeeId
import com.cramsan.edifikana.lib.model.eventLog.EventLogEntryId
import com.cramsan.edifikana.lib.model.eventLog.EventLogEventType
import com.cramsan.edifikana.lib.model.property.PropertyId
import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

/**
 * Model representing an event log entry.
 */
@NetworkModel
@Serializable
@JsonSchema.Description("An entry in a property's event log, e.g. a guest, incident, or delivery record.")
data class EventLogEntryNetworkResponse(
    @JsonSchema.Description("Unique identifier of the event log entry.")
    val id: EventLogEntryId,
    @SerialName("employee_id")
    @JsonSchema.Description(
        "Identifier of the employee who logged the entry, or null if recorded for an unregistered " +
            "employee (see [fallbackEmployeeName]).",
    )
    val employeeId: EmployeeId?,
    @SerialName("fallback_employee_name")
    @JsonSchema.Description(
        "Freeform name of the employee when [employeeId] is null, or null if not applicable.",
    )
    val fallbackEmployeeName: String?,
    @SerialName("property_id")
    @JsonSchema.Description("Identifier of the property the entry belongs to.")
    val propertyId: PropertyId,
    @JsonSchema.Description("Type of the event log entry.")
    val type: EventLogEventType,
    @SerialName("fallback_type")
    @JsonSchema.Description("Freeform event type used when [type] is OTHER, or null if not applicable.")
    val fallbackEventType: String?,
    @JsonSchema.Description("ISO-8601 timestamp when the event occurred.")
    @JsonSchema.Format("date-time")
    val timestamp: Instant,
    @JsonSchema.Description("Title of the event log entry.")
    @JsonSchema.Example("\"Package delivered to front desk\"")
    val title: String,
    @JsonSchema.Description("Description of the event log entry, or null if none.")
    val description: String?,
    @JsonSchema.Description("Identifier of the unit the entry is associated with.")
    val unit: UnitId,
) : ResponseBody

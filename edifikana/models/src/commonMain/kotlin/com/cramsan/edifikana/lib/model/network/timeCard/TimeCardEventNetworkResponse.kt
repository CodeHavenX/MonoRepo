package com.cramsan.edifikana.lib.model.network.timeCard

import com.cramsan.edifikana.lib.model.employee.EmployeeId
import com.cramsan.edifikana.lib.model.property.PropertyId
import com.cramsan.edifikana.lib.model.timeCard.TimeCardEventId
import com.cramsan.edifikana.lib.model.timeCard.TimeCardEventType
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

/**
 * Model representing a time card event.
 */
@NetworkModel
@Serializable
@JsonSchema.Description("A clock-in, clock-out, or other time card event for an employee.")
data class TimeCardEventNetworkResponse(
    @SerialName("id")
    @JsonSchema.Description("Unique identifier of the time card event.")
    val id: TimeCardEventId,
    @SerialName("employee_id")
    @JsonSchema.Description(
        "Identifier of the employee this event belongs to, or null if recorded for an unregistered " +
            "employee (see [fallbackEmployeeName]).",
    )
    val employeeId: EmployeeId?,
    @SerialName("fallback_employee_name")
    @JsonSchema.Description(
        "Freeform name of the employee when [employeeId] is null, or null if not applicable.",
    )
    val fallbackEmployeeName: String?,
    @SerialName("property_id")
    @JsonSchema.Description("Identifier of the property the event was recorded at.")
    val propertyId: PropertyId,
    @JsonSchema.Description("Type of the time card event.")
    val type: TimeCardEventType,
    @SerialName("image_url")
    @JsonSchema.Description("URL of a photo captured with the event, or null if none.")
    @JsonSchema.Format("uri")
    val imageUrl: String?,
    @JsonSchema.Description("ISO-8601 timestamp when the event occurred.")
    @JsonSchema.Format("date-time")
    val timestamp: Instant,
) : ResponseBody

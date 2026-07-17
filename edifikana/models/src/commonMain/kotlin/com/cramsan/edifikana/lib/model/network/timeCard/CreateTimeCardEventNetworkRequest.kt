package com.cramsan.edifikana.lib.model.network.timeCard

import com.cramsan.edifikana.lib.model.employee.EmployeeId
import com.cramsan.edifikana.lib.model.property.PropertyId
import com.cramsan.edifikana.lib.model.timeCard.TimeCardEventType
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.RequestBody
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request model for creating a time card event.
 */
@NetworkModel
@Serializable
@JsonSchema.Description("Request payload to record a new time card event.")
data class CreateTimeCardEventNetworkRequest(
    @SerialName("employee_id")
    @JsonSchema.Description("Identifier of the employee this event belongs to.")
    val employeeId: EmployeeId,
    @SerialName("fallback_employee_name")
    @JsonSchema.Description("Freeform employee name, used when the employee has no registered account.")
    val fallbackEmployeeName: String?,
    @SerialName("property_id")
    @JsonSchema.Description("Identifier of the property the event is recorded at.")
    val propertyId: PropertyId,
    @JsonSchema.Description("Type of the time card event.")
    val type: TimeCardEventType,
    @SerialName("image_url")
    @JsonSchema.Description("URL of a photo captured with the event, or null if none.")
    @JsonSchema.Format("uri")
    val imageUrl: String?,
) : RequestBody

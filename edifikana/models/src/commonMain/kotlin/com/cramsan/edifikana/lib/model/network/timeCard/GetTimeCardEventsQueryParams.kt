package com.cramsan.edifikana.lib.model.network.timeCard

import com.cramsan.edifikana.lib.model.employee.EmployeeId
import com.cramsan.edifikana.lib.model.property.PropertyId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.QueryParam
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable

/**
 * Query parameters for fetching time card events.
 *
 * @param employeeId Optional ID of the employee to filter events.
 * @param propertyId ID of the property to fetch events from.
 */
@NetworkModel
@Serializable
@JsonSchema.Description("Query parameters for listing time card events, requiring a property id.")
data class GetTimeCardEventsQueryParams(
    @JsonSchema.Description("Optional employee identifier to filter events by.")
    val employeeId: EmployeeId?,
    @JsonSchema.Description("Identifier of the property to fetch events from.")
    val propertyId: PropertyId,
) : QueryParam

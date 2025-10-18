package com.cramsan.edifikana.lib.model.network

import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.QueryParam
import kotlinx.serialization.Serializable

/**
 * Query parameters for fetching time card events.
 *
 * @param employeeId Optional ID of the employee to filter events.
 * @param propertyId ID of the property to fetch events from.
 */
@NetworkModel
@Serializable
data class GetTimeCardEventsQueryParams(
    val employeeId: EmployeeId?,
    val propertyId: PropertyId,
) : QueryParam

package com.cramsan.edifikana.lib.model.network

import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.TimeCardEventType
import com.cramsan.framework.annotations.NetworkModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request model for creating a time card event.
 */
@NetworkModel
@Serializable
data class CreateTimeCardEventNetworkRequest(
    @SerialName("employee_id")
    val employeeId: EmployeeId,
    @SerialName("fallback_employee_name")
    val fallbackEmployeeName: String?,
    @SerialName("property_id")
    val propertyId: PropertyId,
    val type: TimeCardEventType,
    @SerialName("image_url")
    val imageUrl: String?,
)

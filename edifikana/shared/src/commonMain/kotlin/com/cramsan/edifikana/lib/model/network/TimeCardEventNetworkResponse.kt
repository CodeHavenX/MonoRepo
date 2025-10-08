package com.cramsan.edifikana.lib.model.network

import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.TimeCardEventId
import com.cramsan.edifikana.lib.model.TimeCardEventType
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Model representing a time card event.
 */
@NetworkModel
@Serializable
data class TimeCardEventNetworkResponse(
    @SerialName("id")
    val id: TimeCardEventId,
    @SerialName("employee_id")
    val employeeId: EmployeeId?,
    @SerialName("fallback_employee_name")
    val fallbackEmployeeName: String?,
    @SerialName("property_id")
    val propertyId: PropertyId,
    val type: TimeCardEventType,
    @SerialName("image_url")
    val imageUrl: String?,
    val timestamp: Long,
) : ResponseBody

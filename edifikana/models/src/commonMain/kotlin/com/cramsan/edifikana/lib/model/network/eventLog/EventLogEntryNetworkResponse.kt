package com.cramsan.edifikana.lib.model.network.eventLog

import com.cramsan.edifikana.lib.model.employee.EmployeeId
import com.cramsan.edifikana.lib.model.eventLog.EventLogEntryId
import com.cramsan.edifikana.lib.model.eventLog.EventLogEventType
import com.cramsan.edifikana.lib.model.property.PropertyId
import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

/**
 * Model representing an event log entry.
 */
@NetworkModel
@Serializable
data class EventLogEntryNetworkResponse(
    val id: EventLogEntryId,
    @SerialName("employee_id")
    val employeeId: EmployeeId?,
    @SerialName("fallback_employee_name")
    val fallbackEmployeeName: String?,
    @SerialName("property_id")
    val propertyId: PropertyId,
    val type: EventLogEventType,
    @SerialName("fallback_type")
    val fallbackEventType: String?,
    val timestamp: Instant,
    val title: String,
    val description: String?,
    val unit: UnitId,
) : ResponseBody

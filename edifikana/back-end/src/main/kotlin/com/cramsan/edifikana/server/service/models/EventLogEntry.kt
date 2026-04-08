package com.cramsan.edifikana.server.service.models

import com.cramsan.edifikana.lib.model.employee.EmployeeId
import com.cramsan.edifikana.lib.model.eventLog.EventLogEntryId
import com.cramsan.edifikana.lib.model.eventLog.EventLogEventType
import com.cramsan.edifikana.lib.model.property.PropertyId
import com.cramsan.edifikana.lib.model.unit.UnitId
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Domain model representing an entry in the event log.
 */
@OptIn(ExperimentalTime::class)
data class EventLogEntry(
    val id: EventLogEntryId,
    val employeeId: EmployeeId?,
    val fallbackEmployeeName: String?,
    val propertyId: PropertyId,
    val type: EventLogEventType,
    val fallbackEventType: String?,
    val timestamp: Instant,
    val title: String,
    val description: String?,
    val unit: UnitId,
)

package com.cramsan.edifikana.server.service.models

import com.cramsan.edifikana.lib.model.employee.EmployeeId
import com.cramsan.edifikana.lib.model.property.PropertyId
import com.cramsan.edifikana.lib.model.timeCard.TimeCardEventId
import com.cramsan.edifikana.lib.model.timeCard.TimeCardEventType
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Domain model representing a time card event such as clocking in or clocking out.
 */
@OptIn(ExperimentalTime::class)
data class TimeCardEvent(
    val id: TimeCardEventId,
    val employeeId: EmployeeId,
    val fallbackEmployeeName: String?,
    val propertyId: PropertyId,
    val type: TimeCardEventType,
    val imageUrl: String?,
    val timestamp: Instant,
)

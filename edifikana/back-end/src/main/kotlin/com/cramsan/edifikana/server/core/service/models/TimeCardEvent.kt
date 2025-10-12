package com.cramsan.edifikana.server.core.service.models

import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.TimeCardEventId
import com.cramsan.edifikana.lib.model.TimeCardEventType
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

package com.cramsan.edifikana.server.core.service.models.requests

import com.cramsan.edifikana.lib.model.EventLogEventType
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.StaffId
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Domain model representing a request to create an event log entry.
 */
@OptIn(ExperimentalTime::class)
data class CreateEventLogEntryRequest(
    val staffId: StaffId?,
    val fallbackStaffName: String?,
    val propertyId: PropertyId,
    val type: EventLogEventType,
    val fallbackEventType: String?,
    val timestamp: Instant,
    val title: String,
    val description: String?,
    val unit: String,
)

package com.cramsan.edifikana.server.core.service.models

import com.cramsan.edifikana.lib.model.EventLogEventType
import kotlinx.datetime.Instant

/**
 * Domain model representing an entry in the event log.
 */
data class EventLogEntry(
    val id: EventLogEntryId,
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

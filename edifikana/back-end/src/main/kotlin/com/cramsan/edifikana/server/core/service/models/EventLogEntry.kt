package com.cramsan.edifikana.server.core.service.models

import com.cramsan.edifikana.lib.model.EventLogEntryId
import com.cramsan.edifikana.lib.model.EventLogEventType
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.StaffId
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Domain model representing an entry in the event log.
 */
@OptIn(ExperimentalTime::class)
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

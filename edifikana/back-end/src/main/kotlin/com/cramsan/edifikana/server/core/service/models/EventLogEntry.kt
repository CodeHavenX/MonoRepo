package com.cramsan.edifikana.server.core.service.models

import kotlinx.datetime.Instant

/**
 * Domain model representing an entry in the event log.
 */
data class EventLogEntry(
    val id: EventLogEntryId,
    val staffId: StaffId?,
    val time: Instant,
    val title: String,
)

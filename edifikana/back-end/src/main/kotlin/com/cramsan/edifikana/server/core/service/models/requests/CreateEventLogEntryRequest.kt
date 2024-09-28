package com.cramsan.edifikana.server.core.service.models.requests

import com.cramsan.edifikana.server.core.service.models.StaffId
import kotlinx.datetime.Instant

/**
 * Domain model representing a request to create an event log entry.
 */
data class CreateEventLogEntryRequest(
    val staffId: StaffId?,
    val time: Instant,
    val title: String,
)

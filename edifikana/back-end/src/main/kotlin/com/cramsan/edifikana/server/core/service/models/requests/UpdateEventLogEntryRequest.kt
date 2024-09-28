package com.cramsan.edifikana.server.core.service.models.requests

import com.cramsan.edifikana.server.core.service.models.EventLogEntryId
import com.cramsan.edifikana.server.core.service.models.StaffId
import kotlinx.datetime.Instant

/**
 * Domain model representing a request to update an event log entry.
 */
data class UpdateEventLogEntryRequest(
    val id: EventLogEntryId,
    val staffId: StaffId?,
    val time: Instant?,
    val title: String?,
)

package com.cramsan.edifikana.server.core.service.models.requests

import com.cramsan.edifikana.server.core.service.models.EventLogEntryId

/**
 * Domain model representing a request to delete an event log entry.
 */
data class DeleteEventLogEntryRequest(
    val id: EventLogEntryId,
)

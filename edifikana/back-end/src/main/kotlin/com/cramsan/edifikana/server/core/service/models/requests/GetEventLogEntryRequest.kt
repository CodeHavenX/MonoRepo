package com.cramsan.edifikana.server.core.service.models.requests

import com.cramsan.edifikana.lib.model.EventLogEntryId

/**
 * Request to get an event log entry.
 */
data class GetEventLogEntryRequest(
    val id: EventLogEntryId,
)

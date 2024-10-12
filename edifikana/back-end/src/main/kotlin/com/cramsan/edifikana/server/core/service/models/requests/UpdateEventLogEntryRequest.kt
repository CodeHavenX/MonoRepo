package com.cramsan.edifikana.server.core.service.models.requests

import com.cramsan.edifikana.lib.model.EventLogEventType
import com.cramsan.edifikana.lib.model.EventLogEntryId

/**
 * Domain model representing a request to update an event log entry.
 */
data class UpdateEventLogEntryRequest(
    val id: EventLogEntryId,
    val type: EventLogEventType?,
    val fallbackEventType: String?,
    val summary: String?,
    val description: String?,
    val unit: String?,
)

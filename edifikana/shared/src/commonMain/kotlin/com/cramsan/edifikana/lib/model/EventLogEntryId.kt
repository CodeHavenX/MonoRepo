package com.cramsan.edifikana.lib.model

import kotlinx.serialization.Serializable

/**
 * Domain model representing an event log entry ID.
 */
@Serializable
data class EventLogEntryId(val eventLogEntryId: String) {
    override fun toString(): String = eventLogEntryId
}

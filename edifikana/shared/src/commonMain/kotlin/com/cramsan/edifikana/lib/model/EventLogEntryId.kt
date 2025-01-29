package com.cramsan.edifikana.lib.model

import kotlin.jvm.JvmInline

/**
 * Domain model representing an event log entry ID.
 */
@JvmInline
value class EventLogEntryId(val eventLogEntryId: String) {
    override fun toString(): String = eventLogEntryId
}

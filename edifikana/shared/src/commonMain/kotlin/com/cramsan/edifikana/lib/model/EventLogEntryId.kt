package com.cramsan.edifikana.lib.model

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Domain model representing an event log entry ID.
 */
@JvmInline
@Serializable
value class EventLogEntryId(val eventLogEntryId: String) {
    override fun toString(): String = eventLogEntryId
}

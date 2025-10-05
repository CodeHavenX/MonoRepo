package com.cramsan.edifikana.lib.model

import com.cramsan.framework.annotations.api.PathParam
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Domain model representing an event log entry ID.
 */
@JvmInline
@Serializable
value class EventLogEntryId(val eventLogEntryId: String) : PathParam {
    override fun toString(): String = eventLogEntryId
}

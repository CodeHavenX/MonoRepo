package com.cramsan.edifikana.lib.model.eventLog

import com.cramsan.framework.annotations.api.PathParam
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Domain model representing an event log entry ID.
 */
@JvmInline
@Serializable
@JsonSchema.Description("Unique identifier of an event log entry.")
@JsonSchema.Example("\"evt_a1b2c3d4\"")
value class EventLogEntryId(val eventLogEntryId: String) : PathParam {
    override fun toString(): String = eventLogEntryId
}

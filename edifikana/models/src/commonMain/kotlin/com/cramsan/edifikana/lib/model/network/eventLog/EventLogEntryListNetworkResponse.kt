package com.cramsan.edifikana.lib.model.network.eventLog

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable

/**
 * Model representing an event log entry.
 */
@NetworkModel
@Serializable
@JsonSchema.Description("A list of event log entries.")
data class EventLogEntryListNetworkResponse(
    @JsonSchema.Description("The event log entries matching the request.")
    val content: List<EventLogEntryNetworkResponse>,
) : ResponseBody

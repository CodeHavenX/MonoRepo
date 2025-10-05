package com.cramsan.edifikana.lib.model.network

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import kotlinx.serialization.Serializable

/**
 * Model representing an event log entry.
 */
@NetworkModel
@Serializable
data class EventLogEntryListNetworkResponse(
    val content: List<EventLogEntryNetworkResponse>,
) : ResponseBody

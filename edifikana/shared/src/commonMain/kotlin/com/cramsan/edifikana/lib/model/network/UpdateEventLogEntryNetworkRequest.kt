package com.cramsan.edifikana.lib.model.network

import com.cramsan.edifikana.lib.annotations.NetworkModel
import com.cramsan.edifikana.lib.model.EventLogEventType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request to create a new event log entry.
 */
@NetworkModel
@Serializable
data class UpdateEventLogEntryNetworkRequest(
    val type: EventLogEventType?,
    @SerialName("fallback_type")
    val fallbackEventType: String?,
    val title: String?,
    val description: String?,
    val unit: String?,
)

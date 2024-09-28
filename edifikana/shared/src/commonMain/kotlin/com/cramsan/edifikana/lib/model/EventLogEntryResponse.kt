package com.cramsan.edifikana.lib.model

import com.cramsan.edifikana.lib.annotations.NetworkModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Model representing an event log entry.
 */
@NetworkModel
@Serializable
data class EventLogEntryResponse(
    @SerialName("id")
    val id: String,
)

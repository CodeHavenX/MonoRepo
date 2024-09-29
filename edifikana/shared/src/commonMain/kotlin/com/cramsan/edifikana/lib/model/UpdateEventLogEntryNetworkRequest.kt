package com.cramsan.edifikana.lib.model

import com.cramsan.edifikana.lib.annotations.NetworkModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request to create a new event log entry.
 */
@NetworkModel
@Serializable
data class UpdateEventLogEntryNetworkRequest(
    @SerialName("title")
    val title: String,
    @SerialName("staff_id")
    val staffId: String,
    @SerialName("time")
    val time: Long,
)

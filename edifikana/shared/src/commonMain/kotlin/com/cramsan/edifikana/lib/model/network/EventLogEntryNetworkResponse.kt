package com.cramsan.edifikana.lib.model.network

import com.cramsan.edifikana.lib.annotations.NetworkModel
import com.cramsan.edifikana.lib.model.EventLogEventType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Model representing an event log entry.
 */
@NetworkModel
@Serializable
data class EventLogEntryNetworkResponse(
    val id: String,
    @SerialName("staff_id")
    val staffId: String?,
    @SerialName("fallback_staff_name")
    val fallbackStaffName: String?,
    @SerialName("property_id")
    val propertyId: String,
    val type: EventLogEventType,
    @SerialName("fallback_type")
    val fallbackEventType: String?,
    val timestamp: Long,
    val title: String,
    val description: String?,
    val unit: String,
)
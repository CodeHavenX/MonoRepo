package com.cramsan.edifikana.server.core.datastore.supabase.models

import com.cramsan.edifikana.lib.model.EventLogEventType
import com.cramsan.edifikana.server.core.datastore.supabase.SupabaseModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Entity representing an event log entry.
 */
@Serializable
@SupabaseModel
data class EventLogEntryEntity(
    val id: String,
    @SerialName("staff_id")
    val staffId: String?,
    @SerialName("fallback_staff_name")
    val fallbackStaffName: String?,
    @SerialName("property_id")
    val propertyId: String,
    val type: EventLogEventType,
    @SerialName("fallback_event_type")
    val fallbackEventType: String?,
    val timestamp: Long,
    val title: String,
    val description: String?,
    val unit: String,
) {
    companion object {
        const val COLLECTION = "event_log_entries"
    }

    /**
     * Entity representing a new event log entry.
     */
    @Serializable
    @SupabaseModel
    data class CreateEventLogEntryEntity(
        @SerialName("staff_id")
        val staffId: String?,
        @SerialName("fallback_staff_name")
        val fallbackStaffName: String?,
        @SerialName("property_id")
        val propertyId: String,
        val type: EventLogEventType,
        @SerialName("fallback_event_type")
        val fallbackEventType: String?,
        val timestamp: Long,
        val title: String,
        val description: String?,
        val unit: String,
    )
}

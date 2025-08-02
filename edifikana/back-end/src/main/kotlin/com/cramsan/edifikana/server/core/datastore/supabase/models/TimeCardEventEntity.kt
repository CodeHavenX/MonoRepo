package com.cramsan.edifikana.server.core.datastore.supabase.models

import com.cramsan.edifikana.lib.model.TimeCardEventType
import com.cramsan.framework.annotations.SupabaseModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Supabase entity representing a time card event.
 */
@Serializable
@SupabaseModel
data class TimeCardEventEntity(
    val id: String,
    @SerialName("staff_id")
    val staffId: String?,
    @SerialName("fallback_staff_name")
    val fallbackStaffName: String?,
    @SerialName("property_id")
    val propertyId: String,
    val type: TimeCardEventType,
    @SerialName("image_url")
    val imageUrl: String?,
    val timestamp: Long,
) {
    companion object {
        const val COLLECTION = "time_card_events"
    }

    /**
     * Supabase entity representing creating a new time card event.
     */
    @Serializable
    @SupabaseModel
    data class CreateTimeCardEventEntity(
        @SerialName("staff_id")
        val staffId: String?,
        @SerialName("fallback_staff_name")
        val fallbackStaffName: String?,
        @SerialName("property_id")
        val propertyId: String,
        val type: TimeCardEventType,
        @SerialName("image_url")
        val imageUrl: String?,
        val timestamp: Long,
    )
}

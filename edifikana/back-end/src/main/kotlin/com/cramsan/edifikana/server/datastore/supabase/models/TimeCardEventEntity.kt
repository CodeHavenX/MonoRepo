package com.cramsan.edifikana.server.datastore.supabase.models

import com.cramsan.edifikana.lib.model.TimeCardEventType
import com.cramsan.framework.annotations.SupabaseModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

/**
 * Supabase entity representing a time card event.
 */
@Serializable
@SupabaseModel
data class TimeCardEventEntity(
    val id: String,
    @SerialName("employee_id")
    val employeeId: String?,
    @SerialName("fallback_employee_name")
    val fallbackEmployeeName: String?,
    @SerialName("property_id")
    val propertyId: String,
    val type: TimeCardEventType,
    @SerialName("image_url")
    val imageUrl: String?,
    val timestamp: Long,
    @SerialName("deleted_at")
    val deletedAt: Instant? = null,
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
        @SerialName("employee_id")
        val employeeId: String?,
        @SerialName("fallback_employee_name")
        val fallbackEmployeeName: String?,
        @SerialName("property_id")
        val propertyId: String,
        val type: TimeCardEventType,
        @SerialName("image_url")
        val imageUrl: String?,
        val timestamp: Long,
    )
}

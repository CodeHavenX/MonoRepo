package com.cramsan.edifikana.server.datastore.supabase.models

import com.cramsan.framework.annotations.SupabaseModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

/**
 * Supabase entity representing a notification.
 */
@Serializable
@SupabaseModel
data class NotificationEntity(
    @SerialName("id")
    val id: String,
    @SerialName("recipient_user_id")
    val recipientUserId: String?,
    @SerialName("notification_type")
    val notificationType: String,
    @SerialName("description")
    val description: String,
    @SerialName("is_read")
    val isRead: Boolean,
    @SerialName("created_at")
    val createdAt: Instant,
    @SerialName("read_at")
    val readAt: Instant?,
    @SerialName("invite_id")
    val inviteId: String?,
) {
    /**
     * Supabase entity representing a create notification request.
     */
    @Serializable
    @SupabaseModel
    data class Create(
        @SerialName("recipient_user_id")
        val recipientUserId: String?,
        @SerialName("notification_type")
        val notificationType: String,
        @SerialName("description")
        val description: String,
        @SerialName("invite_id")
        val inviteId: String,
    )

    companion object {
        const val COLLECTION = "notifications"
    }
}

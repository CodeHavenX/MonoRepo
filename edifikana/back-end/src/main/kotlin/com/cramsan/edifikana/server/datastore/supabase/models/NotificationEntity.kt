package com.cramsan.edifikana.server.datastore.supabase.models

import com.cramsan.edifikana.lib.model.common.Email
import com.cramsan.edifikana.lib.model.invite.InviteId
import com.cramsan.edifikana.lib.model.notification.NotificationId
import com.cramsan.edifikana.lib.model.notification.NotificationType
import com.cramsan.edifikana.lib.model.user.UserId
import com.cramsan.framework.annotations.DatabaseModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

/**
 * Supabase entity representing a notification.
 */
@Serializable
@DatabaseModel
data class NotificationEntity(
    @SerialName("id")
    val id: NotificationId,
    @SerialName("recipient_user_id")
    val recipientUserId: UserId?,
    @SerialName("recipient_email")
    val recipientEmail: Email?,
    @SerialName("notification_type")
    val notificationType: NotificationType,
    @SerialName("description")
    val description: String,
    @SerialName("is_read")
    val isRead: Boolean,
    @SerialName("created_at")
    val createdAt: Instant,
    @SerialName("read_at")
    val readAt: Instant?,
    @SerialName("invite_id")
    val inviteId: InviteId?,
    @SerialName("deleted_at")
    val deletedAt: Instant? = null,
) {
    /**
     * Supabase entity representing a create notification request.
     */
    @Serializable
    @DatabaseModel
    data class Create(
        @SerialName("recipient_user_id")
        val recipientUserId: UserId?,
        @SerialName("recipient_email")
        val recipientEmail: Email?,
        @SerialName("notification_type")
        val notificationType: NotificationType,
        @SerialName("description")
        val description: String,
        @SerialName("invite_id")
        val inviteId: InviteId,
    )

    companion object {
        const val COLLECTION = "notifications"
    }
}

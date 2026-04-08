package com.cramsan.edifikana.server.service.models

import com.cramsan.edifikana.lib.model.invite.InviteId
import com.cramsan.edifikana.lib.model.notification.NotificationId
import com.cramsan.edifikana.lib.model.notification.NotificationType
import com.cramsan.edifikana.lib.model.user.UserId
import kotlin.time.Instant

/**
 * Domain model representing a notification.
 */
data class Notification(
    val id: NotificationId,
    val recipientUserId: UserId?,
    val recipientEmail: String?,
    val notificationType: NotificationType,
    val description: String,
    val isRead: Boolean,
    val createdAt: Instant,
    val readAt: Instant?,
    val inviteId: InviteId?,
)

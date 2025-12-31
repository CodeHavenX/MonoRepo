package com.cramsan.edifikana.server.service.models

import com.cramsan.edifikana.lib.model.NotificationId
import com.cramsan.edifikana.lib.model.NotificationType
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.UserId
import kotlin.time.Instant

/**
 * Domain model representing a notification.
 */
data class Notification(
    val notificationId: NotificationId,
    val recipientUserId: UserId?,
    val recipientEmail: String,
    val organizationId: OrganizationId,
    val notificationType: NotificationType,
    val isRead: Boolean,
    val createdAt: Instant,
    val readAt: Instant?,
)

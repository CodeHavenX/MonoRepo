package com.cramsan.edifikana.client.lib.models

import com.cramsan.edifikana.lib.model.InviteId
import com.cramsan.edifikana.lib.model.NotificationId
import com.cramsan.edifikana.lib.model.NotificationType
import com.cramsan.edifikana.lib.model.OrganizationId
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Client-side model representing a notification.
 */
@OptIn(ExperimentalTime::class)
data class Notification(
    val id: NotificationId,
    val type: NotificationType,
    val description: String,
    val isRead: Boolean,
    val createdAt: Instant,
    val readAt: Instant?,
    val inviteId: InviteId?,
)

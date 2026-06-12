package com.cramsan.edifikana.lib.model.network.notification

import com.cramsan.edifikana.lib.model.invite.InviteId
import com.cramsan.edifikana.lib.model.notification.NotificationId
import com.cramsan.edifikana.lib.model.notification.NotificationType
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import kotlinx.serialization.Serializable
import kotlin.time.Instant

/**
 * Response model for a notification.
 */
@NetworkModel
@Serializable
data class NotificationNetworkResponse(
    val id: NotificationId,
    val notificationType: NotificationType,
    val description: String,
    val isRead: Boolean,
    val createdAt: Instant,
    val readAt: Instant?,
    val inviteId: InviteId?,
) : ResponseBody

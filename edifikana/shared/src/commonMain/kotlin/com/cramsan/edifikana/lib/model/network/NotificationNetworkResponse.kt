package com.cramsan.edifikana.lib.model.network

import com.cramsan.edifikana.lib.model.InviteId
import com.cramsan.edifikana.lib.model.NotificationId
import com.cramsan.edifikana.lib.model.NotificationType
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import kotlinx.serialization.Serializable

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
    val createdAt: Long,
    val readAt: Long?,
    val inviteId: InviteId?,
) : ResponseBody

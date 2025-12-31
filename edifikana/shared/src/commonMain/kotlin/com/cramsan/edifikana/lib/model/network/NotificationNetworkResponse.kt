package com.cramsan.edifikana.lib.model.network

import com.cramsan.edifikana.lib.model.NotificationId
import com.cramsan.edifikana.lib.model.NotificationType
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import kotlinx.serialization.Serializable

/**
 * Response model for a notification.
 */
@NetworkModel
@Serializable
data class NotificationNetworkResponse(
    val notificationId: NotificationId,
    val organizationId: OrganizationId,
    val notificationType: NotificationType,
    val isRead: Boolean,
    val createdAt: Long,
    val readAt: Long?,
) : ResponseBody

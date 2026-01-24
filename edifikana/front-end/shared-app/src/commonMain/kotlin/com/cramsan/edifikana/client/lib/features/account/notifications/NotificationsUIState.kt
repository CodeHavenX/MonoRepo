package com.cramsan.edifikana.client.lib.features.account.notifications

import com.cramsan.edifikana.lib.model.InviteId
import com.cramsan.edifikana.lib.model.NotificationId
import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI state of the Notifications feature.
 *
 * This class models the top level state of the page.
 * For modeling more specific details of the page, see the respective UI model class.
 */
data class NotificationsUIState(
    val title: String?,
    val isLoading: Boolean,
    val notifications: List<NotificationItemUIModel>,
) : ViewModelUIState {
    companion object {
        val Initial = NotificationsUIState(
            title = null,
            isLoading = true,
            notifications = emptyList(),
        )
    }
}

/**
 * UI model to represent a notification in the notifications list.
 */
sealed interface NotificationItemUIModel {
    val id: NotificationId
    val isRead: Boolean
    val createdAt: String
    val description: String
}

/**
 * UI model for an invite notification.
 */
data class InviteNotificationUIModel(
    override val id: NotificationId,
    override val isRead: Boolean,
    override val createdAt: String,
    override val description: String,
    val inviteId: InviteId,
) : NotificationItemUIModel

/**
 * UI model for a system notification.
 */
data class SystemNotificationUIModel(
    override val id: NotificationId,
    override val isRead: Boolean,
    override val createdAt: String,
    override val description: String,
    val message: String,
) : NotificationItemUIModel

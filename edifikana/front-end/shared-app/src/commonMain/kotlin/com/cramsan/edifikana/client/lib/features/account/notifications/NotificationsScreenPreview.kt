package com.cramsan.edifikana.client.lib.features.account.notifications

import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.ui.theme.AppTheme
import com.cramsan.edifikana.lib.model.InviteId
import com.cramsan.edifikana.lib.model.NotificationId
import com.cramsan.edifikana.lib.model.OrganizationId
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Preview for the Notifications feature screen.
 */
@Preview
@Composable
private fun NotificationsScreenPreview() = AppTheme {
    NotificationsContent(
        content = NotificationsUIState(
            title = "Notifications",
            isLoading = false,
            notifications = listOf(
                InviteNotificationUIModel(
                    id = NotificationId("1"),
                    isRead = false,
                    createdAt = "2024-01-22",
                    description = "You have been invited to join Acme Corp",
                    inviteId = InviteId("invite1"),
                ),
                SystemNotificationUIModel(
                    id = NotificationId("2"),
                    isRead = false,
                    createdAt = "2024-01-21",
                    description = "System maintenance scheduled",
                    message = "System maintenance scheduled",
                ),
                SystemNotificationUIModel(
                    id = NotificationId("2"),
                    isRead = true,
                    createdAt = "2024-01-21",
                    description = "System maintenance completed",
                    message = "System maintenance completed",
                ),
            ),
        ),
        onBackSelected = {},
        onAcceptInvite = {},
        onDeclineInvite = {},
        onMarkAsRead = {},
        onDeleteNotification = {},
    )
}

@Preview
@Composable
private fun NotificationsScreenEmptyPreview() = AppTheme {
    NotificationsContent(
        content = NotificationsUIState(
            title = "Notifications",
            isLoading = false,
            notifications = emptyList(),
        ),
        onBackSelected = {},
        onAcceptInvite = {},
        onDeclineInvite = {},
        onMarkAsRead = {},
        onDeleteNotification = {},
    )
}

package com.cramsan.edifikana.client.lib.features.account.notifications

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.sharp.Cancel
import androidx.compose.material.icons.sharp.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.edifikana.client.ui.components.EdifikanaTopBar
import com.cramsan.edifikana.lib.model.InviteId
import com.cramsan.edifikana.lib.model.NotificationId
import com.cramsan.framework.core.compose.ui.ObserveViewModelEvents
import com.cramsan.ui.components.LoadingAnimationOverlay
import com.cramsan.ui.components.ScreenLayout
import com.cramsan.ui.theme.Padding
import com.cramsan.ui.theme.Size
import edifikana_lib.Res
import edifikana_lib.string_cancel
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Notifications screen.
 *
 * This function provides the boilerplate needed to wire up the screen within the rest of the
 * application. This includes observing the view model's state and event flows and rendering the screen.
 */
@Composable
fun NotificationsScreen(
    modifier: Modifier = Modifier,
    viewModel: NotificationsViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    /**
     * For other possible lifecycle events, see the [Lifecycle.Event] documentation.
     */
    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        viewModel.initialize()
    }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        // Call this feature's viewModel
    }

    ObserveViewModelEvents(viewModel) { event ->
        when (event) {
            NotificationsEvent.Noop -> Unit
        }
    }

    // Render the screen
    NotificationsContent(
        content = uiState,
        onBackSelected = { viewModel.onBackSelected() },
        onAcceptInvite = { inviteId -> viewModel.acceptInvite(inviteId) },
        onDeclineInvite = { inviteId -> viewModel.declineInvite(inviteId) },
        onMarkAsRead = { notificationId -> viewModel.markAsRead(notificationId) },
        onDeleteNotification = { notificationId -> viewModel.deleteNotification(notificationId) },
        modifier = modifier,
    )
}

/**
 * Content of the Notifications screen.
 */
@Composable
internal fun NotificationsContent(
    content: NotificationsUIState,
    onBackSelected: () -> Unit,
    onAcceptInvite: (InviteId) -> Unit,
    onDeclineInvite: (InviteId) -> Unit,
    onMarkAsRead: (NotificationId) -> Unit,
    onDeleteNotification: (NotificationId) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            EdifikanaTopBar(
                title = content.title,
                onNavigationIconSelected = onBackSelected,
            )
        },
    ) { innerPadding ->
        ScreenLayout(
            modifier = Modifier.padding(innerPadding).fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(Padding.X_SMALL),
            sectionContent = { sectionModifier ->
                if (content.notifications.isEmpty() && !content.isLoading) {
                    EmptyNotificationsMessage(modifier = sectionModifier)
                } else {
                    content.notifications.forEach { notification ->
                        when (notification) {
                            is InviteNotificationUIModel -> {
                                InviteNotificationItem(
                                    notification = notification,
                                    onAccept = { onAcceptInvite(notification.inviteId) },
                                    onDecline = { onDeclineInvite(notification.inviteId) },
                                    modifier = sectionModifier,
                                )
                            }
                            is SystemNotificationUIModel -> {
                                SystemNotificationItem(
                                    notification = notification,
                                    onMarkAsRead = { onMarkAsRead(notification.id) },
                                    onDeleteNotification = { onDeleteNotification(notification.id) },
                                    modifier = sectionModifier,
                                )
                            }
                        }
                    }
                }
            },
            buttonContent = { buttonModifier ->
            },
            overlay = {
                LoadingAnimationOverlay(content.isLoading)
            }
        )
    }
}

@Composable
private fun EmptyNotificationsMessage(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Padding.MEDIUM),
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "No notifications",
                modifier = Modifier.size(Size.xx_large),
                tint = MaterialTheme.colorScheme.outline,
            )
            Text(
                text = "No notifications",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.outline,
            )
        }
    }
}

@Composable
private fun InviteNotificationItem(
    notification: InviteNotificationUIModel,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .border(
                width = 1.dp,
                color = if (notification.isRead) Color.LightGray else MaterialTheme.colorScheme.primary,
                shape = MaterialTheme.shapes.medium,
            )
            .padding(Padding.MEDIUM),
        verticalArrangement = Arrangement.spacedBy(Padding.SMALL),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
        ) {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = "Invitation",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(Size.large),
            )
            Spacer(Modifier.size(Padding.MEDIUM))
            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = "Organization Invite",
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = notification.description,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = notification.createdAt,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline,
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Padding.SMALL, Alignment.End),
        ) {
            OutlinedButton(
                onClick = onDecline,
            ) {
                Text("Decline")
            }
            Button(
                onClick = onAccept,
            ) {
                Text("Accept")
            }
        }
    }
}

@Composable
private fun SystemNotificationItem(
    notification: SystemNotificationUIModel,
    onMarkAsRead: () -> Unit,
    onDeleteNotification: (NotificationId) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .border(
                width = 1.dp,
                color = if (notification.isRead) Color.LightGray else MaterialTheme.colorScheme.primary,
                shape = MaterialTheme.shapes.medium,
            )
            .padding(Padding.MEDIUM)
            .clickable { onMarkAsRead() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
    ) {
        Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = "System notification",
            tint = if (notification.isRead) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(Size.large),
        )
        Spacer(Modifier.size(Padding.MEDIUM))
        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = notification.message,
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = notification.createdAt,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline,
            )
        }
        IconButton(onClick = { onDeleteNotification(notification.id) }) {
            Icon(
                imageVector = Icons.Sharp.Close,
                contentDescription = stringResource(Res.string.string_cancel),
                //tint = Color.White,
                modifier = Modifier.fillMaxSize()
                    .padding(5.dp)
            )
        }
    }
}

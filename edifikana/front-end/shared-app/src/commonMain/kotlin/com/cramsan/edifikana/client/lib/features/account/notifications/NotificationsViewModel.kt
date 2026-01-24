package com.cramsan.edifikana.client.lib.features.account.notifications

import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.edifikana.client.lib.managers.NotificationManager
import com.cramsan.edifikana.client.lib.managers.OrganizationManager
import com.cramsan.edifikana.client.lib.models.Notification
import com.cramsan.edifikana.lib.model.InviteId
import com.cramsan.edifikana.lib.model.NotificationId
import com.cramsan.edifikana.lib.model.NotificationType
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime

/**
 * ViewModel for the Notifications screen.
 **/
@OptIn(ExperimentalTime::class)
class NotificationsViewModel(
    dependencies: ViewModelDependencies,
    private val authManager: AuthManager,
    private val notificationManager: NotificationManager,
) : BaseViewModel<NotificationsEvent, NotificationsUIState>(
    dependencies,
    NotificationsUIState.Initial,
    TAG,
) {

    /**
     * Initialize the ViewModel and load notifications.
     */
    fun initialize() {
        viewModelScope.launch {
            updateUiState {
                it.copy(
                    title = "Notifications",
                    isLoading = true,
                )
            }
            loadNotifications()
        }
    }

    private suspend fun loadNotifications() {
        val notificationsResult = notificationManager.getNotifications()

        notificationsResult
            .onFailure { throwable ->
                emitWindowEvent(
                    EdifikanaWindowsEvent.ShowSnackbar(
                        "Failed to load notifications: ${throwable.message ?: "Unknown error"}"
                    )
                )
                updateUiState {
                    it.copy(
                        isLoading = false,
                        notifications = emptyList(),
                    )
                }
            }
            .onSuccess { notifications ->
                val uiModels = notifications.mapNotNull { notification ->
                    notification.toUIModel()
                }
                updateUiState {
                    it.copy(
                        isLoading = false,
                        notifications = uiModels,
                    )
                }
            }
    }

    private fun Notification.toUIModel(): NotificationItemUIModel? {
        return when (type) {
            NotificationType.INVITE -> {
                val inviteId = inviteId ?: return null
                InviteNotificationUIModel(
                    id = id,
                    isRead = isRead,
                    createdAt = formatTimestamp(createdAt),
                    description = description,
                    inviteId = inviteId,
                )
            }
            NotificationType.SYSTEM -> {
                SystemNotificationUIModel(
                    id = id,
                    isRead = isRead,
                    createdAt = formatTimestamp(createdAt),
                    description = description,
                    message = description,
                )
            }
        }
    }

    private fun formatTimestamp(instant: kotlin.time.Instant): String {
        return instant.toString()
    }

    /**
     * Accept an invitation.
     */
    fun acceptInvite(inviteId: InviteId) {
        viewModelScope.launch {
            updateUiState { it.copy(isLoading = true) }

            authManager.acceptInvite(inviteId)
                .onSuccess {
                    emitWindowEvent(
                        EdifikanaWindowsEvent.ShowSnackbar("Invitation accepted successfully")
                    )
                    loadNotifications()
                }
                .onFailure { throwable ->
                    emitWindowEvent(
                        EdifikanaWindowsEvent.ShowSnackbar(
                            "Failed to accept invitation: ${throwable.message ?: "Unknown error"}"
                        )
                    )
                    updateUiState { it.copy(isLoading = false) }
                }
        }
    }

    /**
     * Decline an invitation.
     */
    fun declineInvite(inviteId: InviteId) {
        viewModelScope.launch {
            updateUiState { it.copy(isLoading = true) }

            authManager.declineInvite(inviteId)
                .onSuccess {
                    emitWindowEvent(
                        EdifikanaWindowsEvent.ShowSnackbar("Invitation declined")
                    )
                    loadNotifications()
                }
                .onFailure { throwable ->
                    emitWindowEvent(
                        EdifikanaWindowsEvent.ShowSnackbar(
                            "Failed to decline invitation: ${throwable.message ?: "Unknown error"}"
                        )
                    )
                    updateUiState { it.copy(isLoading = false) }
                }
        }
    }

    /**
     * Mark a notification as read.
     */
    fun markAsRead(notificationId: NotificationId) {
        viewModelScope.launch {
            notificationManager.markAsRead(notificationId)
                .onFailure { throwable ->
                    emitWindowEvent(
                        EdifikanaWindowsEvent.ShowSnackbar(
                            "Failed to mark notification as read: ${throwable.message ?: "Unknown error"}"
                        )
                    )
                }
                .onSuccess {
                    loadNotifications()
                }
        }
    }

    /**
     * Delete a notification.
     */
    fun deleteNotification(notificationId: NotificationId) {
        viewModelScope.launch {
            notificationManager.deleteNotification(notificationId)
                .onFailure { throwable ->
                    emitWindowEvent(
                        EdifikanaWindowsEvent.ShowSnackbar(
                            "Failed to delete notification: ${throwable.message ?: "Unknown error"}"
                        )
                    )
                }
                .onSuccess {
                    loadNotifications()
                }
        }
    }

    /**
     * Trigger the back event.
     */
    fun onBackSelected() {
        viewModelScope.launch {
            emitWindowEvent(EdifikanaWindowsEvent.NavigateBack)
        }
    }

    companion object {
        private const val TAG = "NotificationsViewModel"
    }
}

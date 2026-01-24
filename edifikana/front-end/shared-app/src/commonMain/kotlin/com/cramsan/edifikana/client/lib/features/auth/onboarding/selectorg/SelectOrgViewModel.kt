package com.cramsan.edifikana.client.lib.features.auth.onboarding.selectorg

import com.cramsan.edifikana.client.lib.features.auth.AuthDestination
import com.cramsan.edifikana.client.lib.features.window.EdifikanaNavGraphDestination
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.edifikana.client.lib.managers.NotificationManager
import com.cramsan.edifikana.client.lib.models.Notification
import com.cramsan.edifikana.lib.model.InviteId
import com.cramsan.edifikana.lib.model.NotificationType
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.logging.logI
import kotlinx.coroutines.launch

/**
 * ViewModel for the SelectOrg screen.
 */
class SelectOrgViewModel(
    private val authManager: AuthManager,
    private val notificationManager: NotificationManager,
    dependencies: ViewModelDependencies,
) : BaseViewModel<SelectOrgEvent, SelectOrgUIState>(
    dependencies,
    SelectOrgUIState.Default,
    TAG,
) {

    /**
     * Initialize the ViewModel by fetching notifications.
     */
    fun initialize() {
        logI(TAG, "Initializing SelectOrgViewModel")
        viewModelScope.launch {
            updateUiState { it.copy(isLoading = true) }
            val notificationResult = notificationManager.getNotifications()

            if (notificationResult.isFailure) {
                logI(TAG, "Failed to fetch notifications: ${notificationResult.exceptionOrNull()}")
                updateUiState { it.copy(isLoading = false) }
                return@launch
            }

            val notifications = notificationResult.getOrDefault(emptyList()).mapNotNull {
                it.toNotificationItemUIModel()
            }

            updateUiState {
                it.copy(
                    isLoading = false,
                    inviteList = notifications,
                )
            }
        }
    }

    private fun Notification.toNotificationItemUIModel(): InviteItemUIModel? {
        if (type != NotificationType.INVITE || inviteId == null) {
            return null
        }
        return InviteItemUIModel(
            description = this.description,
            inviteId = this.inviteId,
        )
    }

    /**
     * Handle create organization option click.
     */
    fun createOrganization() {
        logI(TAG, "Create workspace clicked")
        viewModelScope.launch {
            emitWindowEvent(
                EdifikanaWindowsEvent.NavigateToScreen(
                    AuthDestination.CreateNewOrgDestination,
                )
            )
        }
    }

    /**
     * Request to show sign out confirmation dialog.
     */
    fun requestSignOut() {
        logI(TAG, "Sign out requested")
        viewModelScope.launch {
            emitEvent(SelectOrgEvent.ShowSignOutConfirmation)
        }
    }

    /**
     * Confirm sign out and perform the actual sign out logic.
     */
    fun confirmSignOut() {
        logI(TAG, "Sign out confirmed")
        viewModelScope.launch {
            authManager.signOut()
            emitWindowEvent(
                EdifikanaWindowsEvent.NavigateToNavGraph(
                    EdifikanaNavGraphDestination.AuthNavGraphDestination,
                    clearStack = true,
                )
            )
        }
    }

    /**
     * Request to show join organization confirmation dialog for the given invite ID.
     */
    fun requestJoinOrganization(inviteId: InviteId) {
        logI(TAG, "Join organization requested for inviteId: $inviteId")
        viewModelScope.launch {
            emitEvent(SelectOrgEvent.ShowJoinOrgConfirmation(inviteId))
        }
    }

    /**
     * Accept the invite for the given invite ID.
     */
    fun acceptInvite(inviteId: InviteId) {
        logI(TAG, "Accept invite clicked for inviteId: $inviteId")
        viewModelScope.launch {
            updateUiState { it.copy(isLoading = true) }
            val acceptResult = authManager.acceptInvite(inviteId)
            if (acceptResult.isFailure) {
                logI(TAG, "Failed to accept invite: ${acceptResult.exceptionOrNull()}")
                updateUiState { it.copy(isLoading = false) }
                return@launch
            }

            emitWindowEvent(
                EdifikanaWindowsEvent.NavigateToNavGraph(
                    EdifikanaNavGraphDestination.HomeNavGraphDestination,
                    clearStack = true,
                )
            )
        }
    }

    companion object {
        private const val TAG = "SelectOrgViewModel"
    }
}

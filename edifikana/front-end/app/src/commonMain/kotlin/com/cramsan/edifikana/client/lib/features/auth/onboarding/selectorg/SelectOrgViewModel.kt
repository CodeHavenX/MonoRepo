package com.cramsan.edifikana.client.lib.features.auth.onboarding.selectorg

import com.cramsan.edifikana.client.lib.features.auth.AuthDestination
import com.cramsan.edifikana.client.lib.features.window.EdifikanaNavGraphDestination
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.edifikana.client.lib.managers.NotificationManager
import com.cramsan.edifikana.client.lib.models.Notification
import com.cramsan.edifikana.lib.model.invite.InviteId
import com.cramsan.edifikana.lib.model.notification.NotificationType
import com.cramsan.framework.annotations.FrontendViewModel
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.logging.logI
import kotlinx.coroutines.launch

/**
 * ViewModel for the SelectOrg screen.
 */
@FrontendViewModel
class SelectOrgViewModel(
    private val authManager: AuthManager,
    private val notificationManager: NotificationManager,
    dependencies: ViewModelDependencies,
) : BaseViewModel<Nothing, SelectOrgUIState>(
    dependencies,
    SelectOrgUIState.Default,
    TAG,
) {
    /**
     * Initialize the ViewModel by fetching notifications.
     */
    fun initialize() {
        logI(TAG, "Initializing SelectOrgViewModel")
        viewModelCoroutineScope.launch {
            updateUiState { it.copy(isLoading = true) }
            val notificationResult = notificationManager.getNotifications()

            if (notificationResult.isFailure) {
                logI(TAG, "Failed to fetch notifications: ${notificationResult.exceptionOrNull()}")
                updateUiState { it.copy(isLoading = false) }
                return@launch
            }

            val notifications =
                notificationResult.getOrDefault(emptyList()).mapNotNull {
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
        viewModelCoroutineScope.launch {
            emitWindowEvent(
                EdifikanaWindowsEvent.NavigateToScreen(
                    AuthDestination.CreateNewOrgDestination,
                ),
            )
        }
    }

    /**
     * Shows the sign out confirmation dialog.
     */
    fun requestSignOut() {
        logI(TAG, "Sign out requested")
        viewModelCoroutineScope.launch {
            updateUiState { it.copy(dialog = SelectOrgDialogState.ConfirmSignOut) }
        }
    }

    /**
     * Confirm sign out and perform the actual sign out logic.
     */
    fun confirmSignOut() {
        logI(TAG, "Sign out confirmed")
        viewModelCoroutineScope.launch {
            authManager.signOut()
            emitWindowEvent(
                EdifikanaWindowsEvent.NavigateToNavGraph(
                    EdifikanaNavGraphDestination.AuthNavGraphDestination,
                    clearStack = true,
                ),
            )
        }
    }

    /**
     * Shows the join organization confirmation dialog for the given invite ID.
     */
    fun requestJoinOrganization(inviteId: InviteId) {
        logI(TAG, "Join organization requested for inviteId: $inviteId")
        viewModelCoroutineScope.launch {
            updateUiState { it.copy(dialog = SelectOrgDialogState.ConfirmJoinOrg(inviteId)) }
        }
    }

    /**
     * Dismisses the active dialog without action.
     */
    fun dismissDialog() {
        viewModelCoroutineScope.launch {
            updateUiState { it.copy(dialog = SelectOrgDialogState.None) }
        }
    }

    /**
     * Accept the invite for the given invite ID.
     */
    fun acceptInvite(inviteId: InviteId) {
        logI(TAG, "Accept invite clicked for inviteId: $inviteId")
        viewModelCoroutineScope.launch {
            updateUiState { it.copy(isLoading = true, dialog = SelectOrgDialogState.None) }
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
                ),
            )
        }
    }

    companion object {
        private const val TAG = "SelectOrgViewModel"
    }
}

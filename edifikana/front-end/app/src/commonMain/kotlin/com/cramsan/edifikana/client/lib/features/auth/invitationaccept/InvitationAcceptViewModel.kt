package com.cramsan.edifikana.client.lib.features.auth.invitationaccept

import com.cramsan.edifikana.client.lib.features.auth.AuthDestination
import com.cramsan.edifikana.client.lib.features.window.EdifikanaNavGraphDestination
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.edifikana.client.lib.managers.NotificationManager
import com.cramsan.edifikana.lib.model.invite.InviteId
import com.cramsan.edifikana.lib.model.notification.NotificationType
import com.cramsan.framework.annotations.FrontendViewModel
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.core.compose.resources.StringProvider
import edifikana_lib.Res
import edifikana_lib.error_message_unexpected_error
import edifikana_lib.invitation_accept_screen_error_invalid_invite
import kotlinx.coroutines.launch

/**
 * ViewModel backing both the invitation landing screen and the invitation accept/decline screen.
 *
 * Account creation and sign-in are delegated entirely to the existing Sign Up / Sign In / OTP
 * flows — this ViewModel never collects credentials. It only resolves session state, surfaces an
 * invitation summary sourced from the matching invite notification (there is no endpoint to
 * resolve structured invite details by token), and calls accept/decline once a session exists.
 */
@FrontendViewModel
class InvitationAcceptViewModel(
    dependencies: ViewModelDependencies,
    private val authManager: AuthManager,
    private val notificationManager: NotificationManager,
    private val stringProvider: StringProvider,
) : BaseViewModel<InvitationAcceptEvent, InvitationAcceptUIState>(
    dependencies,
    InvitationAcceptUIState.Initial,
    TAG,
) {
    /**
     * Loads session state for the invitation identified by [inviteId].
     *
     * Sets [InvitationAcceptUIState.error] if [inviteId] is blank (a malformed deep link).
     * Otherwise sets [InvitationAcceptUIState.isUserSignedIn], and when signed in, also
     * populates [InvitationAcceptUIState.invitationSummary] from the matching invite
     * notification, if one is found. No match is not an error — the screen falls back to
     * generic copy.
     *
     * When [redirectIfSignedIn] is true and the user turns out to already be signed in,
     * immediately navigates to [AuthDestination.InvitationAcceptConfirmDestination] instead of
     * leaving the landing screen's Create Account / Sign In buttons showing. Only the landing
     * screen opts into this — the confirm screen also calls this function to refresh its own
     * state and must not re-navigate to itself.
     */
    fun loadInvitation(inviteId: InviteId, redirectIfSignedIn: Boolean = false) {
        viewModelCoroutineScope.launch {
            updateUiState { it.copy(isLoading = true, error = null) }

            if (inviteId.id.isBlank()) {
                updateUiState {
                    it.copy(
                        isLoading = false,
                        error = stringProvider.getString(Res.string.invitation_accept_screen_error_invalid_invite),
                    )
                }
                return@launch
            }

            val signedIn =
                authManager
                    .isSignedIn()
                    .onFailure {
                        updateUiState {
                            it.copy(
                                isLoading = false,
                                error = stringProvider.getString(Res.string.error_message_unexpected_error),
                            )
                        }
                        return@launch
                    }.getOrThrow()

            val summary = if (signedIn) findInvitationSummary(inviteId) else null

            updateUiState {
                it.copy(
                    isLoading = false,
                    isUserSignedIn = signedIn,
                    invitationSummary = summary,
                )
            }

            if (signedIn && redirectIfSignedIn) {
                emitWindowEvent(
                    EdifikanaWindowsEvent.NavigateToScreen(
                        AuthDestination.InvitationAcceptConfirmDestination(inviteId),
                        clearTop = true,
                    ),
                )
            }
        }
    }

    /**
     * Accepts the invitation identified by [inviteId] for the current, already-authenticated
     * session, then navigates to the home nav graph with the back stack cleared.
     */
    fun acceptInvitation(inviteId: InviteId) {
        viewModelCoroutineScope.launch {
            updateUiState { it.copy(isLoading = true, error = null) }
            authManager
                .acceptInvite(inviteId)
                .onFailure { e ->
                    updateUiState {
                        it.copy(
                            isLoading = false,
                            error =
                            e.message
                                ?: stringProvider.getString(Res.string.error_message_unexpected_error),
                        )
                    }
                    return@launch
                }
            updateUiState { it.copy(isLoading = false) }
            emitWindowEvent(
                EdifikanaWindowsEvent.NavigateToNavGraph(
                    EdifikanaNavGraphDestination.HomeNavGraphDestination,
                    clearStack = true,
                ),
            )
        }
    }

    /**
     * Declines the invitation identified by [inviteId] for the current, already-authenticated
     * session, then navigates to the sign-in screen.
     */
    fun declineInvitation(inviteId: InviteId) {
        viewModelCoroutineScope.launch {
            updateUiState { it.copy(isLoading = true, error = null) }
            authManager
                .declineInvite(inviteId)
                .onFailure { e ->
                    updateUiState {
                        it.copy(
                            isLoading = false,
                            error =
                            e.message
                                ?: stringProvider.getString(Res.string.error_message_unexpected_error),
                        )
                    }
                    return@launch
                }
            updateUiState { it.copy(isLoading = false) }
            emitWindowEvent(
                EdifikanaWindowsEvent.NavigateToScreen(
                    AuthDestination.SignInDestination(),
                    clearStack = true,
                ),
            )
        }
    }

    /**
     * Navigates to the sign-up screen, deferring invite acceptance until after account creation
     * and OTP verification complete.
     */
    fun navigateToSignUp(inviteId: InviteId) {
        viewModelCoroutineScope.launch {
            emitWindowEvent(
                EdifikanaWindowsEvent.NavigateToScreen(
                    AuthDestination.SignUpDestination(
                        userEmail = "",
                        inviteId = inviteId,
                    ),
                ),
            )
        }
    }

    /**
     * Navigates to the sign-in screen, deferring invite acceptance until after sign-in
     * completes.
     */
    fun navigateToSignIn(inviteId: InviteId) {
        viewModelCoroutineScope.launch {
            emitWindowEvent(
                EdifikanaWindowsEvent.NavigateToScreen(
                    AuthDestination.SignInDestination(inviteId = inviteId),
                ),
            )
        }
    }

    private suspend fun findInvitationSummary(inviteId: InviteId): String? =
        notificationManager
            .getNotifications()
            .getOrNull()
            ?.firstOrNull { it.type == NotificationType.INVITE && it.inviteId == inviteId }
            ?.description

    companion object {
        private const val TAG = "InvitationAcceptViewModel"
    }
}

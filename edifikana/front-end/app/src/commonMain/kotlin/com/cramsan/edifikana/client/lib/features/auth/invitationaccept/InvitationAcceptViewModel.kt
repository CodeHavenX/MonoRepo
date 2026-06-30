package com.cramsan.edifikana.client.lib.features.auth.invitationaccept

import com.cramsan.edifikana.client.lib.features.auth.AuthDestination
import com.cramsan.edifikana.client.lib.features.window.EdifikanaNavGraphDestination
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.edifikana.client.lib.managers.OrganizationManager
import com.cramsan.edifikana.lib.model.invite.InviteId
import com.cramsan.framework.annotations.FrontendViewModel
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.core.compose.resources.StringProvider
import edifikana_lib.Res
import edifikana_lib.error_message_unexpected_error
import edifikana_lib.invitation_accept_screen_error_empty_name
import edifikana_lib.invitation_accept_screen_error_invalid_invite
import edifikana_lib.invitation_accept_screen_error_password_too_short
import edifikana_lib.invitation_accept_screen_error_passwords_do_not_match
import kotlinx.coroutines.launch

/**
 * ViewModel for the invitation accept screen.
 *
 * Handles both the new-user path (sign-up + accept) and the existing-user path (accept only).
 */
@FrontendViewModel
class InvitationAcceptViewModel(
    dependencies: ViewModelDependencies,
    private val authManager: AuthManager,
    private val organizationManager: OrganizationManager,
    private val stringProvider: StringProvider,
) : BaseViewModel<InvitationAcceptEvent, InvitationAcceptUIState>(
    dependencies,
    InvitationAcceptUIState.Initial,
    TAG,
) {
    /**
     * Loads and validates the invitation identified by [inviteId].
     *
     * Sets [InvitationAcceptUIState.isUserSignedIn] based on the current session state.
     * On an invalid or expired token, sets [InvitationAcceptUIState.error].
     */
    fun loadInvitation(inviteId: String) {
        viewModelCoroutineScope.launch {
            updateUiState { it.copy(isLoading = true, error = null) }
            val signedIn = authManager.isSignedIn()
                .onFailure {
                    updateUiState {
                        it.copy(
                            isLoading = false,
                            error = stringProvider.getString(Res.string.error_message_unexpected_error),
                        )
                    }
                    return@launch
                }
                .getOrThrow()

            // Full invite detail population (org name, role, inviter) requires GET /invites/resolve
            // which is not yet available — tracked in issue #392.
            updateUiState {
                it.copy(
                    isLoading = false,
                    isUserSignedIn = signedIn,
                    isInviteValid = inviteId.isNotBlank(),
                )
            }
        }
    }

    /** Updates the full-name field in the form. */
    fun updateFullName(name: String) {
        viewModelCoroutineScope.launch {
            updateUiState { it.copy(fullName = name) }
        }
    }

    /** Updates the password field in the form. */
    fun updatePassword(password: String) {
        viewModelCoroutineScope.launch {
            updateUiState { it.copy(password = password) }
        }
    }

    /** Updates the confirm-password field in the form. */
    fun updateConfirmPassword(password: String) {
        viewModelCoroutineScope.launch {
            updateUiState { it.copy(confirmPassword = password) }
        }
    }

    /**
     * Accepts the invitation as a new user.
     *
     * Validates the form, calls [AuthManager.signUp] then [AuthManager.acceptInvite], and
     * navigates to the org-selection screen (if no org exists yet) or the home nav graph.
     */
    fun acceptAsNewUser(inviteId: String) {
        viewModelCoroutineScope.launch {
            val state = uiState.value
            val error = validateNewUserForm(state) ?: run {
                updateUiState { it.copy(isLoading = true, error = null) }

                val parts = state.fullName.trim().split(" ", limit = 2)
                val firstName = parts.firstOrNull().orEmpty()
                val lastName = parts.getOrNull(1).orEmpty()

                authManager
                    .signUp(
                        email = state.inviteEmail,
                        phoneNumber = "",
                        firstName = firstName,
                        lastName = lastName,
                    )
                    .onFailure { e ->
                        updateUiState {
                            it.copy(
                                isLoading = false,
                                error = e.message
                                    ?: stringProvider.getString(Res.string.error_message_unexpected_error),
                            )
                        }
                        return@launch
                    }

                authManager
                    .acceptInvite(InviteId(inviteId))
                    .onFailure { e ->
                        updateUiState {
                            it.copy(
                                isLoading = false,
                                error = e.message
                                    ?: stringProvider.getString(Res.string.error_message_unexpected_error),
                            )
                        }
                        return@launch
                    }

                updateUiState { it.copy(isLoading = false) }
                navigateAfterAccept()
                return@launch
            }
            updateUiState { it.copy(error = error) }
        }
    }

    /**
     * Accepts the invitation for an already-signed-in user.
     *
     * Calls [AuthManager.acceptInvite] and navigates to the home nav graph with the back
     * stack cleared.
     */
    fun acceptAsExistingUser(inviteId: String) {
        viewModelCoroutineScope.launch {
            updateUiState { it.copy(isLoading = true, error = null) }
            authManager
                .acceptInvite(InviteId(inviteId))
                .onFailure { e ->
                    updateUiState {
                        it.copy(
                            isLoading = false,
                            error = e.message
                                ?: stringProvider.getString(Res.string.error_message_unexpected_error),
                        )
                    }
                    return@launch
                }
            updateUiState { it.copy(isLoading = false) }
            emitWindowEvent(
                EdifikanaWindowsEvent.NavigateToNavGraph(
                    EdifikanaNavGraphDestination.HomeNavGraphDestination,
                    clearTop = true,
                ),
            )
        }
    }

    /** Navigates to the sign-in screen, carrying the current invite context implicitly. */
    fun navigateToSignIn() {
        viewModelCoroutineScope.launch {
            emitWindowEvent(EdifikanaWindowsEvent.NavigateToScreen(AuthDestination.SignInDestination))
        }
    }

    private suspend fun validateNewUserForm(state: InvitationAcceptUIState): String? {
        return when {
            state.fullName.isBlank() ->
                stringProvider.getString(Res.string.invitation_accept_screen_error_empty_name)
            state.password.length < MIN_PASSWORD_LENGTH ->
                stringProvider.getString(Res.string.invitation_accept_screen_error_password_too_short)
            state.password != state.confirmPassword ->
                stringProvider.getString(Res.string.invitation_accept_screen_error_passwords_do_not_match)
            else -> null
        }
    }

    private suspend fun navigateAfterAccept() {
        val organizations = organizationManager.getOrganizations().getOrNull()
        if (organizations.isNullOrEmpty()) {
            emitWindowEvent(
                EdifikanaWindowsEvent.NavigateToScreen(
                    AuthDestination.SelectOrgDestination,
                    clearTop = true,
                ),
            )
        } else {
            emitWindowEvent(
                EdifikanaWindowsEvent.NavigateToNavGraph(
                    EdifikanaNavGraphDestination.HomeNavGraphDestination,
                    clearTop = true,
                ),
            )
        }
    }

    companion object {
        private const val MIN_PASSWORD_LENGTH = 8
        private const val TAG = "InvitationAcceptViewModel"
    }
}

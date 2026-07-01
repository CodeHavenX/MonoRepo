package com.cramsan.edifikana.client.lib.features.auth.invitationaccept

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI state for the invitation accept screen.
 */
data class InvitationAcceptUIState(
    val isLoading: Boolean = false,
    val inviteEmail: String = "",
    val orgName: String = "",
    val role: String = "",
    val invitedByName: String = "",
    val fullName: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val error: String? = null,
    val isUserSignedIn: Boolean = false,
    val isInviteValid: Boolean = false,
) : ViewModelUIState {
    companion object {
        /** Loading placeholder shown while the invitation is being validated. */
        val Initial = InvitationAcceptUIState(isLoading = true)
    }
}

package com.cramsan.edifikana.client.lib.features.auth.invitationaccept

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI state shared by the invitation landing screen and the invitation accept/decline screen.
 */
data class InvitationAcceptUIState(
    val isLoading: Boolean = false,
    val invitationSummary: String? = null,
    val error: String? = null,
    val isUserSignedIn: Boolean = false,
) : ViewModelUIState {
    companion object {
        /** Loading placeholder shown while session state is being resolved. */
        val Initial = InvitationAcceptUIState(isLoading = true)
    }
}

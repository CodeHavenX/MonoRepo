package com.cramsan.edifikana.client.lib.features.auth.signup

import com.cramsan.edifikana.lib.model.invite.InviteId
import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * Sign Up UI state.
 */
data class SignUpUIState(
    val isLoading: Boolean,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val policyChecked: Boolean,
    val registerEnabled: Boolean,
    val errorMessages: List<String>?,
    val inviteId: InviteId? = null,
) : ViewModelUIState {
    companion object {
        val Initial =
            SignUpUIState(
                isLoading = false,
                firstName = "",
                lastName = "",
                email = "",
                phoneNumber = "",
                policyChecked = false,
                registerEnabled = false,
                errorMessages = null,
                inviteId = null,
            )
    }
}

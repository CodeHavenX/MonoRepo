package com.cramsan.edifikana.client.lib.features.auth.signup

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
    val errorMessage: List<String>?,
) : ViewModelUIState {

    companion object {
        val Initial = SignUpUIState(
            isLoading = false,
            firstName = "",
            lastName = "",
            email = "",
            phoneNumber = "",
            policyChecked = false,
            registerEnabled = false,
            errorMessage = null,
        )
    }
}

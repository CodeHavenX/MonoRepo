package com.cramsan.edifikana.client.lib.features.auth.signup

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * Sign Up UI state.
 */
data class SignUpUIState(
    val isLoading: Boolean,
    val signUpForm: SignUpFormUIModel,
) : ViewModelUIState {

    companion object {
        val Initial = SignUpUIState(
            isLoading = false,
            signUpForm = SignUpFormUIModel(
                firstName = "",
                lastName = "",
                email = "",
                phoneNumber = "",
                password = "",
                policyChecked = false,
                registerEnabled = false,
                errorMessage = null,
            ),
        )
    }
}

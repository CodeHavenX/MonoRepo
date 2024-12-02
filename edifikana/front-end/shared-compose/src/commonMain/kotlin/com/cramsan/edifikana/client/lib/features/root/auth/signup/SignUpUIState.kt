package com.cramsan.edifikana.client.lib.features.root.auth.signup

/**
 * Sign Up UI state.
 */
data class SignUpUIState(
    val isLoading: Boolean,
    val signUpForm: SignUpFormUIModel,
) {

    companion object {
        val Initial = SignUpUIState(
            isLoading = false,
            signUpForm = SignUpFormUIModel(
                fullName = "",
                username = "",
                password = "",
                policyChecked = false,
                registerEnabled = false,
                errorMessage = null,
            ),
        )
    }
}

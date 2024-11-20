package com.cramsan.edifikana.client.lib.features.root.auth.signinv2

/**
 * Sign in v2 UI state.
 */
data class SignInV2UIState(
    val isLoading: Boolean,
    val signInForm: SignInFormUIModel,
) {

    companion object {
        val Initial = SignInV2UIState(
            isLoading = false,
            signInForm = SignInFormUIModel(
                email = "",
                password = "",
                errorMessage = null,
            ),
        )
    }
}

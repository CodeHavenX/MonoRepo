package com.cramsan.edifikana.client.lib.features.auth.signin

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * Sign in UI state.
 */
data class SignInUIState(
    val isLoading: Boolean,
    val signInForm: SignInFormUIModel,
) : ViewModelUIState {

    companion object {
        val Initial = SignInUIState(
            isLoading = false,
            signInForm = SignInFormUIModel(
                email = "",
                password = "",
                errorMessage = null,
            ),
        )
    }
}

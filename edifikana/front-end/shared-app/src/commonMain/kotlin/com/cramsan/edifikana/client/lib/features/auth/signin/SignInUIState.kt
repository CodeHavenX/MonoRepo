package com.cramsan.edifikana.client.lib.features.auth.signin

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * Sign in UI state.
 */
data class SignInUIState(
    val isLoading: Boolean,
    val email: String,
    val password: String,
    val showPassword: Boolean,
    val errorMessage: String?,
) : ViewModelUIState {

    companion object {
        val Initial = SignInUIState(
            isLoading = false,
            email = "",
            password = "",
            showPassword = false,
            errorMessage = null,
        )
    }
}

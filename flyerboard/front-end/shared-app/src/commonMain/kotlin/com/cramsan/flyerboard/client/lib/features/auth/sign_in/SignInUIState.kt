package com.cramsan.flyerboard.client.lib.features.auth.sign_in

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI state for the Sign In screen.
 */
data class SignInUIState(
    val isLoading: Boolean,
    val email: String,
    val password: String,
) : ViewModelUIState {
    companion object {
        val Initial = SignInUIState(
            isLoading = false,
            email = "",
            password = "",
        )
    }
}

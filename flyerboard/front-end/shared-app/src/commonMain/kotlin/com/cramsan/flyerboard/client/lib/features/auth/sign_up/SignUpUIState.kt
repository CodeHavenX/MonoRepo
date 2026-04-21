package com.cramsan.flyerboard.client.lib.features.auth.sign_up

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI state for the Sign Up screen.
 */
data class SignUpUIState(
    val isLoading: Boolean,
    val email: String,
    val password: String,
) : ViewModelUIState {
    companion object {
        val Initial = SignUpUIState(
            isLoading = false,
            email = "",
            password = "",
        )
    }
}

package com.cramsan.flyerboard.client.lib.features.auth.sign_up

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI state for the Sign Up screen.
 */
data class SignUpUIState(
    val email: String,
    val password: String,
    val confirmPassword: String,
    val isLoading: Boolean,
) : ViewModelUIState {
    companion object {
        val Initial =
            SignUpUIState(
                email = "",
                password = "",
                confirmPassword = "",
                isLoading = false,
            )
    }
}

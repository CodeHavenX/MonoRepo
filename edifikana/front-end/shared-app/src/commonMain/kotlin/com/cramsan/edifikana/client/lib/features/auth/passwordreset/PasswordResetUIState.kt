package com.cramsan.edifikana.client.lib.features.auth.passwordreset

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * Password reset UI state.
 */
data class PasswordResetUIState(
    val isLoading: Boolean,
    val email: String,
    val errorMessages: List<String>?,
) : ViewModelUIState {
    companion object {
        /** Initial state for the password reset screen. */
        val Initial = PasswordResetUIState(
            isLoading = false,
            email = "",
            errorMessages = null,
        )
    }
}

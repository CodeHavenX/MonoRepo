package com.cramsan.edifikana.client.lib.features.auth.passwordresetconfirmation

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * Password reset confirmation UI state.
 */
data class PasswordResetConfirmationUIState(
    val isLoading: Boolean,
    val email: String,
    val errorMessages: List<String>?,
) : ViewModelUIState {
    companion object {
        /** Initial state for the password reset confirmation screen. */
        val Initial = PasswordResetConfirmationUIState(
            isLoading = false,
            email = "",
            errorMessages = null,
        )
    }
}

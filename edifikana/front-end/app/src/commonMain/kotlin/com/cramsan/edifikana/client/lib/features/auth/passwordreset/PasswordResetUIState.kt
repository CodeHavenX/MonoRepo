package com.cramsan.edifikana.client.lib.features.auth.passwordreset

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * Password reset UI state.
 */
sealed class PasswordResetUIState : ViewModelUIState {
    /** The email address entered by the user. */
    abstract val email: String

    /**
     * Stable state — email field is editable, no operation in progress.
     */
    data class Stable(override val email: String = "") : PasswordResetUIState()

    /**
     * Loading state — a reset request is in flight.
     */
    data class Loading(override val email: String) : PasswordResetUIState()

    /**
     * Error state — the reset request failed or validation errors are present.
     */
    data class Error(override val email: String, val messages: List<String>) : PasswordResetUIState()

    companion object {
        /** Initial state for the password reset screen. */
        val Initial: PasswordResetUIState = Stable()
    }
}

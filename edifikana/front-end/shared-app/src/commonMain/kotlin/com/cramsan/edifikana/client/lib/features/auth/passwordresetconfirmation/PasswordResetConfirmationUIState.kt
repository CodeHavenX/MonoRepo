package com.cramsan.edifikana.client.lib.features.auth.passwordresetconfirmation

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * Password reset confirmation UI state.
 */
sealed class PasswordResetConfirmationUIState : ViewModelUIState {
    /** The email address the reset link was sent to. */
    abstract val email: String

    /**
     * Stable state — reset link sent, user can resend.
     */
    data class Stable(override val email: String = "") : PasswordResetConfirmationUIState()

    /**
     * Loading state — a resend request is in flight.
     */
    data class Loading(override val email: String) : PasswordResetConfirmationUIState()

    /**
     * Error state — the resend request failed.
     */
    data class Error(override val email: String, val messages: List<String>) : PasswordResetConfirmationUIState()

    companion object {
        /** Initial state for the password reset confirmation screen. */
        val Initial: PasswordResetConfirmationUIState = Stable()
    }
}

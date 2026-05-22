package com.cramsan.edifikana.client.lib.features.auth.passwordresetconfirmation

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Password reset confirmation event.
 */
sealed class PasswordResetConfirmationEvent : ViewModelEvent {
    /**
     * No operation.
     */
    data object Noop : PasswordResetConfirmationEvent()
}

package com.cramsan.edifikana.client.lib.features.auth.passwordreset

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Password reset event.
 */
sealed class PasswordResetEvent : ViewModelEvent {
    /**
     * No operation.
     */
    data object Noop : PasswordResetEvent()
}

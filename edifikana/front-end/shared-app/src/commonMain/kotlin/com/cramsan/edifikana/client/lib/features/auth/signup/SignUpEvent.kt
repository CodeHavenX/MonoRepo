package com.cramsan.edifikana.client.lib.features.auth.signup

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Sign Up feature event.
 */
sealed class SignUpEvent : ViewModelEvent {
    /**
     * No operation
     */
    data object Noop : SignUpEvent()
}

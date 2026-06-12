package com.cramsan.edifikana.client.lib.features.auth.signin

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Sign in event.
 */
sealed class SignInEvent : ViewModelEvent {
    /**
     * No operation
     */
    data object Noop : SignInEvent()
}

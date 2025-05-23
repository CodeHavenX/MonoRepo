package com.codehavenx.alpaca.frontend.appcore.features.signin

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events for the Sign In screen.
 */
sealed class SignInEvent : ViewModelEvent {
    /**
     * No operation
     */
    data object Noop : SignInEvent()
}

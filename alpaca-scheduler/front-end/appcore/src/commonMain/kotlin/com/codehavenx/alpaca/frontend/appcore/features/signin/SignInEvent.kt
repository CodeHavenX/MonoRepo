package com.codehavenx.alpaca.frontend.appcore.features.signin

import com.codehavenx.alpaca.frontend.appcore.features.application.ApplicationEvent
import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events for the Sign In screen.
 */
sealed class SignInEvent : ViewModelEvent {

    /**
     * No operation
     */
    data object Noop : SignInEvent()

    /**
     * Trigger an application event.
     */
    data class TriggerApplicationEvent(
        val applicationEvent: ApplicationEvent,
    ) : SignInEvent()
}

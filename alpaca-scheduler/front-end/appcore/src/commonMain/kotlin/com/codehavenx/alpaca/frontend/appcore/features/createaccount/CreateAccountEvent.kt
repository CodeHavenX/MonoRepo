package com.codehavenx.alpaca.frontend.appcore.features.createaccount

import com.codehavenx.alpaca.frontend.appcore.features.application.ApplicationEvent

/**
 * Events for the Create Account screen.
 */
sealed class CreateAccountEvent {

    /**
     * No operation
     */
    data object Noop : CreateAccountEvent()

    /**
     * Trigger an application event.
     */
    data class TriggerApplicationEvent(
        val applicationEvent: ApplicationEvent,
    ) : CreateAccountEvent()
}

package com.codehavenx.alpaca.frontend.appcore.features.clients.addclient

import com.codehavenx.alpaca.frontend.appcore.features.application.ApplicationEvent
import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events for the Add Client screen.
 */
sealed class AddClientEvent : ViewModelEvent {

    /**
     * No operation
     */
    data object Noop : AddClientEvent()

    /**
     * Trigger an application event.
     */
    data class TriggerApplicationEvent(
        val applicationEvent: ApplicationEvent,
    ) : AddClientEvent()
}

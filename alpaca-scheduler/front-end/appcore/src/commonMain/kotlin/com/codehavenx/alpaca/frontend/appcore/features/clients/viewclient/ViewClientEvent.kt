package com.codehavenx.alpaca.frontend.appcore.features.clients.viewclient

import com.codehavenx.alpaca.frontend.appcore.features.application.ApplicationEvent
import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events for the View Client screen.
 */
sealed class ViewClientEvent : ViewModelEvent {

    /**
     * No operation
     */
    data object Noop : ViewClientEvent()

    /**
     * Trigger an application event.
     */
    data class TriggerApplicationEvent(
        val applicationEvent: ApplicationEvent,
    ) : ViewClientEvent()
}

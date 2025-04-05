package com.codehavenx.alpaca.frontend.appcore.features.clients.addclient

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events for the Add Client screen.
 */
sealed class AddClientEvent : ViewModelEvent {
    /**
     * No operation
     */
    data object Noop : AddClientEvent()
}

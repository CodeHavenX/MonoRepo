package com.codehavenx.alpaca.frontend.appcore.features.clients.updateclient

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events for the Update Client screen.
 */
sealed class UpdateClientEvent : ViewModelEvent {
    /**
     * No operation
     */
    data object Noop : UpdateClientEvent()
}

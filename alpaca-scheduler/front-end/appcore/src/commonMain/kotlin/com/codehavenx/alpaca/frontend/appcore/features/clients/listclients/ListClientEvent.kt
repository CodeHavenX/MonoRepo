package com.codehavenx.alpaca.frontend.appcore.features.clients.listclients

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events for the List Clients screen.
 */
sealed class ListClientEvent : ViewModelEvent {
    /**
     * No operation
     */
    data object Noop : ListClientEvent()
}

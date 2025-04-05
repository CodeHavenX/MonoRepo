package com.codehavenx.alpaca.frontend.appcore.features.clients.viewclient

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events for the View Client screen.
 */
sealed class ViewClientEvent : ViewModelEvent {
    /**
     * No operation
     */
    data object Noop : ViewClientEvent()
}

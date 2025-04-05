package com.codehavenx.alpaca.frontend.appcore.features.home

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * The events for the home feature.
 */
sealed class HomeEvent : ViewModelEvent {
    /**
     * No operation
     */
    data object Noop : HomeEvent()
}

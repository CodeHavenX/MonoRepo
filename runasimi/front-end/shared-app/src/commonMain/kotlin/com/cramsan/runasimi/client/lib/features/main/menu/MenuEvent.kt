package com.cramsan.runasimi.client.lib.features.main.menu

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events that can be triggered within the domain of the Menu feature.
 *
 * Events are triggered from a ViewModel and are consumed by the UI.
 *
 */
sealed class MenuEvent : ViewModelEvent {
    /**
     * No operation.
     */
    data object Noop : MenuEvent()
}

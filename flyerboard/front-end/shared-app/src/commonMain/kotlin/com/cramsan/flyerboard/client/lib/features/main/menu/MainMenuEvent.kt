package com.cramsan.flyerboard.client.lib.features.main.menu

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events that can be triggered within the domain of the Main Menu feature.
 */
sealed class MainMenuEvent : ViewModelEvent {
    /**
     * No operation
     */
    data object Noop : MainMenuEvent()
}

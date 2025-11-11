package com.cramsan.templatereplaceme.client.lib.features.main.menu

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Sign in event.
 */
sealed class MainMenuEvent : ViewModelEvent {
    /**
     * No operation
     */
    data object Noop : MainMenuEvent()
}

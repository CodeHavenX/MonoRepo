package com.cramsan.framework.sample.shared.features.main.menu

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * MainMenu event.
 */
sealed class MainMenuEvent : ViewModelEvent {
    /**
     * Noop event.
     */
    data object Noop : MainMenuEvent()
}

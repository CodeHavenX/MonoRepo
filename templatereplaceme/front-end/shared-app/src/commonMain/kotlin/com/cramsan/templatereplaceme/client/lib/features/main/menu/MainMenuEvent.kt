package com.cramsan.templatereplaceme.client.lib.features.main.menu

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events emitted by [MainMenuViewModel] and consumed by [MainMenuScreen].
 *
 * Add sealed subclasses here for one-shot actions that shouldn't live in [MainMenuUIState]
 * (e.g., navigation triggers, snackbar messages).
 */
sealed class MainMenuEvent : ViewModelEvent {
    /** Placeholder event — replace with your screen's real events. */
    data object Noop : MainMenuEvent()
}

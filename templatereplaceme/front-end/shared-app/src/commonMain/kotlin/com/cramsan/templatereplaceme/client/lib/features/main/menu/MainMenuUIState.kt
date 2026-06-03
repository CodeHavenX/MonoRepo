package com.cramsan.templatereplaceme.client.lib.features.main.menu

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI state for the [MainMenuScreen].
 *
 * Add fields here to represent the data the screen needs to render.
 * Keep this immutable — the ViewModel produces a new instance for every update.
 */
data class MainMenuUIState(val isLoading: Boolean) : ViewModelUIState {
    companion object {
        /** The state the screen starts in before any data is loaded. */
        val Initial = MainMenuUIState(isLoading = false)
    }
}

package com.cramsan.framework.sample.shared.features.main.menu

import com.cramsan.framework.core.compose.ViewModelUIState
import com.cramsan.framework.sample.shared.features.main.welcome.ThemeSelection

/**
 * Main Menu UI state.
 */
data class MainMenuUIState(val selectedTheme: ThemeSelection? = null) : ViewModelUIState {
    companion object {
        val Initial = MainMenuUIState()
    }
}

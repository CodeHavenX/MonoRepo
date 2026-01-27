package com.cramsan.edifikana.client.lib.features.settings.general

import com.cramsan.framework.core.compose.ViewModelUIState
import com.cramsan.ui.components.themetoggle.SelectedTheme

/**
 * UI State for the Settings screen.
 */
data class SettingsUIState(val selectedTheme: SelectedTheme) : ViewModelUIState {
    companion object {
        val Initial = SettingsUIState(
            selectedTheme = SelectedTheme.SYSTEM_DEFAULT,
        )
    }
}

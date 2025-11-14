package com.cramsan.edifikana.client.lib.features.application

import com.cramsan.framework.core.compose.ViewModelUIState
import com.cramsan.ui.components.themetoggle.SelectedTheme

/**
 * Application UI state.
 */
data class EdifikanaApplicationUIState(
    val applicationLoaded: Boolean,
    val showDebugWindow: Boolean,
    val theme: SelectedTheme,
) : ViewModelUIState {
    companion object {
        /**
         * Initial UI state.
         */
        val Initial = EdifikanaApplicationUIState(
            applicationLoaded = false,
            showDebugWindow = false,
            theme = SelectedTheme.SYSTEM_DEFAULT,
        )
    }
}
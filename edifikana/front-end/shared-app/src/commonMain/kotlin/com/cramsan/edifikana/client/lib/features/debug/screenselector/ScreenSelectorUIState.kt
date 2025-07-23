package com.cramsan.edifikana.client.lib.features.debug.screenselector

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI state of the ScreenSelector feature.
 *
 * This class models the top level state of the page.
 * For modeling more specific details of the page, see the respective UI model class.
 */
data class ScreenSelectorUIState(
    val title: String?,
    val isLoading: Boolean,
) : ViewModelUIState {
    companion object {
        val Initial = ScreenSelectorUIState(
            title = null,
            isLoading = true,
        )
    }
}

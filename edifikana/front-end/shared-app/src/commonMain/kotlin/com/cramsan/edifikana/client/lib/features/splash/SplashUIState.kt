package com.cramsan.edifikana.client.lib.features.splash

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI state of the Splash feature.
 *
 * This class models the top level state of the page.
 * For modeling more specific details of the page, see the respective UI model class.
 */
data class SplashUIState(val isLoading: Boolean) : ViewModelUIState {
    companion object {
        val Initial = SplashUIState(
            isLoading = true,
        )
    }
}

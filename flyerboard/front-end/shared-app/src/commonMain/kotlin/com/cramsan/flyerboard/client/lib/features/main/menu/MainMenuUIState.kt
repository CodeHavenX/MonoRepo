package com.cramsan.flyerboard.client.lib.features.main.menu

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI state for the Main Menu screen.
 */
data class MainMenuUIState(
    val isLoading: Boolean,
    val firstName: String,
    val lastName: String,
) : ViewModelUIState {

    companion object {
        val Initial = MainMenuUIState(
            isLoading = false,
            firstName = "",
            lastName = "",
        )
    }
}

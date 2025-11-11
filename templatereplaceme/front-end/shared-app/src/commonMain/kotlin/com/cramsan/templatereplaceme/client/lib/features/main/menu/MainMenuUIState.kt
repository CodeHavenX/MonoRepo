package com.cramsan.templatereplaceme.client.lib.features.main.menu

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * Sign in UI state.
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

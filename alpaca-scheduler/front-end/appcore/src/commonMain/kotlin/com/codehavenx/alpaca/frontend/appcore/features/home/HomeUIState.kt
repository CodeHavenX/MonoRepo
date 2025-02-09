package com.codehavenx.alpaca.frontend.appcore.features.home

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI Model for the Home screen.
 */
data class HomeUIState(
    val content: HomeUIModel,
    val isLoading: Boolean,
) : ViewModelUIState {
    companion object {
        val Initial = HomeUIState(
            content = HomeUIModel(
                name = "",
            ),
            isLoading = false,
        )
    }
}

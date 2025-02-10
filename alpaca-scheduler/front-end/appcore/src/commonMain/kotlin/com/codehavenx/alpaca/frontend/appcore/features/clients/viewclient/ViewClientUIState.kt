package com.codehavenx.alpaca.frontend.appcore.features.clients.viewclient

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI Model for the View Client screen.
 */
data class ViewClientUIState(
    val content: ViewClientUIModel?,
    val isLoading: Boolean,
) : ViewModelUIState {
    companion object {
        val Initial = ViewClientUIState(
            content = null,
            isLoading = false,
        )
    }
}

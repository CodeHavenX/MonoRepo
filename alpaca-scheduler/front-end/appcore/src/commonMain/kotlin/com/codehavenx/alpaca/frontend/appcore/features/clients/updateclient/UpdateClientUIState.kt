package com.codehavenx.alpaca.frontend.appcore.features.clients.updateclient

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI Model for the Update Client screen.
 */
data class UpdateClientUIState(
    val content: UpdateClientUIModel?,
    val isLoading: Boolean,
) : ViewModelUIState {
    companion object {
        val Initial = UpdateClientUIState(
            content = null,
            isLoading = false,
        )
    }
}

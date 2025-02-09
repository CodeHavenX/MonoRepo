package com.codehavenx.alpaca.frontend.appcore.features.clients.addclient

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI Model for the Add Client screen.
 */
data class AddClientUIModel(
    val name: String,
) : ViewModelUIState {
    companion object {
        val Initial = AddClientUIModel(
            name = "",
        )
    }
}

package com.cramsan.runasimi.client.lib.features.main.menu

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI state of the Menu feature.
 *
 * This class models the top level state of the page.
 * For modeling more specific details of the page, see the respective UI model class.
 */
data class MenuUIState(
    val title: String?,
    val isLoading: Boolean,
) : ViewModelUIState {
    companion object {
        val Initial = MenuUIState(
            title = null,
            isLoading = true,
        )
    }
}

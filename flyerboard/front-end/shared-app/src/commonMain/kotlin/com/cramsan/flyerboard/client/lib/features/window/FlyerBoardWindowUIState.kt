package com.cramsan.flyerboard.client.lib.features.window

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * Window UI state.
 */
data class FlyerBoardWindowUIState(
    val isAuthenticated: Boolean,
) : ViewModelUIState {
    companion object {
        val Initial = FlyerBoardWindowUIState(isAuthenticated = false)
    }
}

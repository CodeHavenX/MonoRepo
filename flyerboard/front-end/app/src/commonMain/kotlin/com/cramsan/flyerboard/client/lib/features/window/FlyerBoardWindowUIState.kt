package com.cramsan.flyerboard.client.lib.features.window

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * Window UI state.
 */
data class FlyerBoardWindowUIState(val authState: AuthState) : ViewModelUIState {
    companion object {
        val Initial = FlyerBoardWindowUIState(authState = AuthState.Unauthenticated)
    }
}

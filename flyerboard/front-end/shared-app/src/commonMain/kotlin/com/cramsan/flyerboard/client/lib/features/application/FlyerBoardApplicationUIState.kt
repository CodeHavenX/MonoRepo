package com.cramsan.flyerboard.client.lib.features.application

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * Application UI state.
 */
data class FlyerBoardApplicationUIState(
    val showDebugWindow: Boolean = false,
) : ViewModelUIState

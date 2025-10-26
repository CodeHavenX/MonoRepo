package com.cramsan.runasimi.client.lib.features.application

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * Application UI state.
 */
data class RunasimiApplicationUIState(
    val showDebugWindow: Boolean = false,
) : ViewModelUIState

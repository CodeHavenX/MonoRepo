package com.cramsan.edifikana.client.lib.features.application

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * Application UI state.
 */
data class EdifikanaApplicationUIState(
    val showDebugWindow: Boolean = false,
) : ViewModelUIState

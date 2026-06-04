package com.cramsan.templatereplaceme.client.lib.app

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * Application UI state.
 */
data class TemplateReplaceMeApplicationUIState(val showDebugWindow: Boolean = false) : ViewModelUIState {
    companion object {
        /** The initial state for [TemplateReplaceMeApplicationUIState]. */
        val Initial = TemplateReplaceMeApplicationUIState()
    }
}

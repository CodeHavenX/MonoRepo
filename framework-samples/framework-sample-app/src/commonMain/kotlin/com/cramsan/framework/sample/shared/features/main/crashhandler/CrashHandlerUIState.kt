package com.cramsan.framework.sample.shared.features.main.crashhandler

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI state of the CrashHandler feature.
 */
data class CrashHandlerUIState(val isInitialized: Boolean) : ViewModelUIState {
    companion object {
        /** Initial state before initialize() is called. */
        val Initial = CrashHandlerUIState(isInitialized = false)
    }
}

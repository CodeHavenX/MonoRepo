package com.cramsan.framework.sample.shared.features.main.dispatcher

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI state of the DispatcherProvider feature.
 */
data class DispatcherUIState(val ioDispatcherInfo: String, val uiDispatcherInfo: String) : ViewModelUIState {
    companion object {
        /** Initial state before any dispatcher is queried. */
        val Initial =
            DispatcherUIState(
                ioDispatcherInfo = "(not queried)",
                uiDispatcherInfo = "(not queried)",
            )
    }
}

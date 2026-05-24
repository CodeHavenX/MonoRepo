package com.cramsan.framework.sample.shared.features.main.threadutil

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI state of the ThreadUtil feature.
 */
data class ThreadUtilUIState(val isUIThread: Boolean?, val isBackgroundThread: Boolean?, val lastAction: String) :
    ViewModelUIState {
    companion object {
        /** Initial state before any method is invoked. */
        val Initial =
            ThreadUtilUIState(
                isUIThread = null,
                isBackgroundThread = null,
                lastAction = "No action taken yet",
            )
    }
}

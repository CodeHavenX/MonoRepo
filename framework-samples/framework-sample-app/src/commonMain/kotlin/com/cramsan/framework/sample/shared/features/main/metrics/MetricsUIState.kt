package com.cramsan.framework.sample.shared.features.main.metrics

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI state of the Metrics feature.
 */
data class MetricsUIState(val lastAction: String) : ViewModelUIState {
    companion object {
        /** Initial state before any method is invoked. */
        val Initial = MetricsUIState(lastAction = "No action taken yet")
    }
}

package com.cramsan.framework.sample.shared.features.main.userevents

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI state of the UserEvents feature.
 */
data class UserEventsUIState(val lastAction: String) : ViewModelUIState {
    companion object {
        /** Initial state before any method is invoked. */
        val Initial = UserEventsUIState(lastAction = "No action taken yet")
    }
}

package com.cramsan.framework.sample.shared.features.main.remoteconfig

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI state of the RemoteConfig feature.
 */
data class RemoteConfigUIState(
    val isLoading: Boolean,
    val isPayloadReady: Boolean,
    val lastAction: String,
    val payloadInfo: String,
) : ViewModelUIState {
    companion object {
        /** Initial state before any method is invoked. */
        val Initial =
            RemoteConfigUIState(
                isLoading = false,
                isPayloadReady = false,
                lastAction = "No action taken yet",
                payloadInfo = "No payload",
            )
    }
}

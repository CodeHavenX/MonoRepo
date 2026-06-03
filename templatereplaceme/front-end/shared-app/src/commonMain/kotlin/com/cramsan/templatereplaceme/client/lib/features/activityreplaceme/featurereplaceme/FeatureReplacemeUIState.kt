package com.cramsan.templatereplaceme.client.lib.features.activityreplaceme.featurereplaceme

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI state for the [FeatureReplacemeScreen].
 *
 * Add fields here to represent the data the screen needs to render.
 * Keep this immutable — the ViewModel produces a new instance for every update.
 */
data class FeatureReplacemeUIState(val isLoading: Boolean) : ViewModelUIState {
    companion object {
        /** The state the screen starts in before any data is loaded. */
        val Initial = FeatureReplacemeUIState(isLoading = false)
    }
}

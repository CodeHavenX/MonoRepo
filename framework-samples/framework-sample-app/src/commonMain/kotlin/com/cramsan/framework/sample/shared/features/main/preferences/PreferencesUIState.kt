package com.cramsan.framework.sample.shared.features.main.preferences

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI state of the Preferences feature.
 */
data class PreferencesUIState(
    val stringValue: String?,
    val intValue: Int?,
    val longValue: Long?,
    val booleanValue: Boolean?,
) : ViewModelUIState {
    companion object {
        /** Initial state with no values loaded. */
        val Initial =
            PreferencesUIState(
                stringValue = null,
                intValue = null,
                longValue = null,
                booleanValue = null,
            )
    }
}

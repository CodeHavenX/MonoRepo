package com.cramsan.framework.sample.shared.features.main.preferences

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events that can be triggered within the domain of the Preferences feature.
 */
sealed class PreferencesEvent : ViewModelEvent {
    /**
     * No operation.
     */
    data object Noop : PreferencesEvent()
}

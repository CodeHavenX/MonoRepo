package com.cramsan.framework.sample.shared.features.main.halt

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events that can be triggered within the domain of the HaltUtil feature.
 *
 * Events are triggered from a ViewModel and are consumed by the UI.
 *
 */
sealed class HaltUtilEvent : ViewModelEvent {
    /**
     * No operation
     */
    data object Noop : HaltUtilEvent()
}

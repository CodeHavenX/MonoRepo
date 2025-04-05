package com.cramsan.edifikana.client.lib.features.debug.main

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events that can be triggered within the domain of the Debug feature.
 *
 * Events are triggered from a ViewModel and are consumed by the UI.
 *
 */
sealed class DebugEvent : ViewModelEvent {
    /**
     * No operation
     */
    data object Noop : DebugEvent()
}

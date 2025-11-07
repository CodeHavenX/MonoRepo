package com.cramsan.edifikana.client.lib.features.home.eventlog

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Represents the UI state of the Event Log screen.
 */
sealed class EventLogEvent : ViewModelEvent {
    /**
     * No operation
     */
    data object Noop : EventLogEvent()
}

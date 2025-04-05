package com.cramsan.edifikana.client.lib.features.main.timecard

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Represents the UI state of the Time Card screen.
 */
sealed class TimeCardEvent : ViewModelEvent {
    /**
     * No operation
     */
    data object Noop : TimeCardEvent()
}

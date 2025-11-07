package com.cramsan.edifikana.client.lib.features.home.timecardemployeelist

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Represents the UI state of the Time Card Employee List screen.
 */
sealed class TimeCardEmployeeListEvent : ViewModelEvent {
    /**
     * No operation
     */
    data object Noop : TimeCardEmployeeListEvent()
}

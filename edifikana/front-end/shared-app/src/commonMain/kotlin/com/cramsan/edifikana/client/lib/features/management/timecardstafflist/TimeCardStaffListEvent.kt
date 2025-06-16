package com.cramsan.edifikana.client.lib.features.management.timecardstafflist

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Represents the UI state of the Time Card Staff List screen.
 */
sealed class TimeCardStaffListEvent : ViewModelEvent {
    /**
     * No operation
     */
    data object Noop : TimeCardStaffListEvent()
}

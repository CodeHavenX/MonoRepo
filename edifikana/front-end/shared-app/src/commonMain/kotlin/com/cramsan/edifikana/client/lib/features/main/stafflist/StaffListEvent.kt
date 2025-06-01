package com.cramsan.edifikana.client.lib.features.main.stafflist

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Represents the UI state of the Staff List screen.
 */
sealed class StaffListEvent : ViewModelEvent {
    /**
     * No operation
     */
    data object Noop : StaffListEvent()
}

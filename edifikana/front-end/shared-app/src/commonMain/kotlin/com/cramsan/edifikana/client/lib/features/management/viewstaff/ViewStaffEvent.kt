package com.cramsan.edifikana.client.lib.features.management.viewstaff

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Represents the UI state of the View Staff screen.
 */
sealed class ViewStaffEvent : ViewModelEvent {
    /**
     * No operation
     */
    data object Noop : ViewStaffEvent()
}

package com.cramsan.edifikana.client.lib.features.main.timecard.viewstaff

import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationEvent
import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Represents the UI state of the View Staff screen.
 */
sealed class ViewStaffEvent : ViewModelEvent {

    /**
     * No operation
     */
    data object Noop : ViewStaffEvent()

    /**
     * Trigger application event.
     */
    data class TriggerEdifikanaApplicationEvent(
        val edifikanaApplicationEvent: EdifikanaApplicationEvent,
    ) : ViewStaffEvent()
}

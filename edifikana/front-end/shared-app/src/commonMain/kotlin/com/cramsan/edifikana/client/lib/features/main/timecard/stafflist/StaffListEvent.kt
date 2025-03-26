package com.cramsan.edifikana.client.lib.features.main.timecard.stafflist

import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationEvent
import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Represents the UI state of the Staff List screen.
 */
sealed class StaffListEvent : ViewModelEvent {

    /**
     * No operation
     */
    data object Noop : StaffListEvent()

    /**
     * Trigger application event.
     */
    data class TriggerEdifikanaApplicationEvent(
        val edifikanaApplicationEvent: EdifikanaApplicationEvent,
    ) : StaffListEvent()
}

package com.cramsan.edifikana.client.lib.features.main.timecard.addstaff

import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationEvent
import com.cramsan.framework.core.compose.ViewModelEvent
import kotlin.random.Random

/**
 * Represents the UI state of the Add Staff screen.
 */
sealed class AddStaffEvent : ViewModelEvent {

    /**
     * No operation
     */
    data object Noop : AddStaffEvent()

    /**
     * Trigger application event.
     */
    data class TriggerEdifikanaApplicationEvent(
        val edifikanaApplicationEvent: EdifikanaApplicationEvent,
        val id: Int = Random.nextInt(),
    ) : AddStaffEvent()
}

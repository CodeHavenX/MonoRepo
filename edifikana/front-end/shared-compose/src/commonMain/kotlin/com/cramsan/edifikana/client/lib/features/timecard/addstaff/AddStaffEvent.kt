package com.cramsan.edifikana.client.lib.features.timecard.addstaff

import com.cramsan.edifikana.client.lib.features.main.MainActivityEvent
import kotlin.random.Random

/**
 * Represents the UI state of the Add Staff screen.
 */
sealed class AddStaffEvent {

    /**
     * No operation
     */
    data object Noop : AddStaffEvent()

    /**
     * Trigger main activity event.
     */
    data class TriggerMainActivityEvent(
        val mainActivityEvent: MainActivityEvent,
        val id: Int = Random.nextInt(),
    ) : AddStaffEvent()
}

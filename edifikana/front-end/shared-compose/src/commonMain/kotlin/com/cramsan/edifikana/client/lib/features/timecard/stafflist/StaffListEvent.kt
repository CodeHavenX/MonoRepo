package com.cramsan.edifikana.client.lib.features.timecard.stafflist

import com.cramsan.edifikana.client.lib.features.main.MainActivityEvent
import kotlin.random.Random

/**
 * Represents the UI state of the Staff List screen.
 */
sealed class StaffListEvent {

    /**
     * No operation
     */
    data object Noop : StaffListEvent()

    /**
     * Trigger main activity event.
     */
    data class TriggerMainActivityEvent(
        val mainActivityEvent: MainActivityEvent,
        val id: Int = Random.nextInt(),
    ) : StaffListEvent()
}

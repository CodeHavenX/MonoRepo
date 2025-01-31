package com.cramsan.edifikana.client.lib.features.root.main.timecard.viewstaff

import com.cramsan.edifikana.client.lib.features.root.main.MainActivityEvent
import kotlin.random.Random

/**
 * Represents the UI state of the View Staff screen.
 */
sealed class ViewStaffEvent {

    /**
     * No operation
     */
    data object Noop : ViewStaffEvent()

    /**
     * Trigger main activity event.
     */
    data class TriggerMainActivityEvent(
        val mainActivityEvent: MainActivityEvent,
        val id: Int = Random.nextInt(),
    ) : ViewStaffEvent()
}

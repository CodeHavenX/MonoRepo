package com.cramsan.edifikana.client.lib.features.root.main.timecard

import com.cramsan.edifikana.client.lib.features.root.main.MainActivityEvent
import kotlin.random.Random

/**
 * Represents the UI state of the Time Card screen.
 */
sealed class TimeCardEvent {

    /**
     * No operation
     */
    data object Noop : TimeCardEvent()

    /**
     * Trigger main activity event.
     */
    data class TriggerMainActivityEvent(
        val mainActivityEvent: MainActivityEvent,
        val id: Int = Random.nextInt(),
    ) : TimeCardEvent()
}

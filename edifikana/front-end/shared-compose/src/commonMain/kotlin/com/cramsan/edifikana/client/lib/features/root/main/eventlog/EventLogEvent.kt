package com.cramsan.edifikana.client.lib.features.root.main.eventlog

import com.cramsan.edifikana.client.lib.features.root.main.MainActivityEvent
import kotlin.random.Random

/**
 * Represents the UI state of the Event Log screen.
 */
sealed class EventLogEvent {

    /**
     * No operation
     */
    data object Noop : EventLogEvent()

    /**
     * Trigger main activity event.
     */
    data class TriggerMainActivityEvent(
        val mainActivityEvent: MainActivityEvent,
        val id: Int = Random.nextInt(),
    ) : EventLogEvent()
}

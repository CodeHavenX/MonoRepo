package com.cramsan.edifikana.client.lib.features.eventlog.viewrecord

import com.cramsan.edifikana.client.lib.features.main.MainActivityEvent
import kotlin.random.Random

/**
 * Represents the UI state of the View Record screen.
 */
sealed class ViewRecordEvent {

    /**
     * No operation event.
     */
    data object Noop : ViewRecordEvent()

    /**
     * Triggers a [MainActivityEvent] event.
     */
    data class TriggerMainActivityEvent(
        val mainActivityEvent: MainActivityEvent,
        val id: Int = Random.nextInt(),
    ) : ViewRecordEvent()
}

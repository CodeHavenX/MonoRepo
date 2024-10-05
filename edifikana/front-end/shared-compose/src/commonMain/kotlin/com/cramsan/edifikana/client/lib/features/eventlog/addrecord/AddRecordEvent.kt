package com.cramsan.edifikana.client.lib.features.eventlog.addrecord

import com.cramsan.edifikana.client.lib.features.main.MainActivityEvent
import kotlin.random.Random

/**
 * Represents the UI state of the Add Record screen.
 */
sealed class AddRecordEvent {

    /**
     * No operation
     */
    data object Noop : AddRecordEvent()

    /**
     * Trigger main activity event.
     */
    data class TriggerMainActivityEvent(
        val mainActivityEvent: MainActivityEvent,
        val id: Int = Random.nextInt(),
    ) : AddRecordEvent()
}

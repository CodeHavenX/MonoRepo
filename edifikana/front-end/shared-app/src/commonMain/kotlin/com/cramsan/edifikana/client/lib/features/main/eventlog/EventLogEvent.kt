package com.cramsan.edifikana.client.lib.features.main.eventlog

import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationEvent
import com.cramsan.framework.core.compose.ViewModelEvent
import kotlin.random.Random

/**
 * Represents the UI state of the Event Log screen.
 */
sealed class EventLogEvent : ViewModelEvent {

    /**
     * No operation
     */
    data object Noop : EventLogEvent()

    /**
     * Trigger application event.
     */
    data class TriggerEdifikanaApplicationEvent(
        val edifikanaApplicationEvent: EdifikanaApplicationEvent,
        val id: Int = Random.nextInt(),
    ) : EventLogEvent()
}

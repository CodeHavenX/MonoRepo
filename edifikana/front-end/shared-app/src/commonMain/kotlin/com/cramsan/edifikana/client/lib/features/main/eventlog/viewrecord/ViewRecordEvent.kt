package com.cramsan.edifikana.client.lib.features.main.eventlog.viewrecord

import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationEvent
import com.cramsan.framework.core.compose.ViewModelEvent
import kotlin.random.Random

/**
 * Represents the UI state of the View Record screen.
 */
sealed class ViewRecordEvent : ViewModelEvent {

    /**
     * No operation event.
     */
    data object Noop : ViewRecordEvent()

    /**
     * Trigger application event.
     */
    data class TriggerEdifikanaApplicationEvent(
        val edifikanaApplicationEvent: EdifikanaApplicationEvent,
        val id: Int = Random.nextInt(),
    ) : ViewRecordEvent()
}

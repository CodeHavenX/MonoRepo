package com.cramsan.edifikana.client.lib.features.main.eventlog.addrecord

import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationEvent
import com.cramsan.framework.core.compose.ViewModelEvent
import kotlin.random.Random

/**
 * Represents the UI state of the Add Record screen.
 */
sealed class AddRecordEvent : ViewModelEvent {

    /**
     * No operation
     */
    data object Noop : AddRecordEvent()

    /**
     * Trigger application event.
     */
    data class TriggerEdifikanaApplicationEvent(
        val edifikanaApplicationEvent: EdifikanaApplicationEvent,
        val id: Int = Random.nextInt(),
    ) : AddRecordEvent()
}

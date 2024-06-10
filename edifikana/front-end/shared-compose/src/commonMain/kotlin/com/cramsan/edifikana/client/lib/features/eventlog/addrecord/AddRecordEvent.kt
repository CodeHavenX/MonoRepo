package com.cramsan.edifikana.client.lib.features.eventlog.addrecord

import com.cramsan.edifikana.client.lib.features.main.MainActivityEvent
import kotlin.random.Random

sealed class AddRecordEvent {
    data object Noop : com.cramsan.edifikana.client.lib.features.eventlog.addrecord.AddRecordEvent()

    data class TriggerMainActivityEvent(
        val mainActivityEvent: MainActivityEvent,
        val id: Int = Random.nextInt(),
    ) : com.cramsan.edifikana.client.lib.features.eventlog.addrecord.AddRecordEvent()
}

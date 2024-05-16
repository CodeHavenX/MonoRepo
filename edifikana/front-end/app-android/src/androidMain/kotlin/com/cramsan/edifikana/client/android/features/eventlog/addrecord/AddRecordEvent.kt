package com.cramsan.edifikana.client.android.features.eventlog.addrecord

import com.cramsan.edifikana.client.android.features.main.MainActivityEvent
import kotlin.random.Random

sealed class AddRecordEvent {
    data object Noop : AddRecordEvent()

    data class TriggerMainActivityEvent(
        val mainActivityEvent: MainActivityEvent,
        val id: Int = Random.nextInt(),
    ) : AddRecordEvent()
}

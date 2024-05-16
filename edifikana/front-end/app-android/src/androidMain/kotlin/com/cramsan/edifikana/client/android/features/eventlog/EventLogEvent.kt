package com.cramsan.edifikana.client.android.features.eventlog

import com.cramsan.edifikana.client.android.features.main.MainActivityEvent
import kotlin.random.Random

sealed class EventLogEvent {
    data object Noop : EventLogEvent()

    data class TriggerMainActivityEvent(
        val mainActivityEvent: MainActivityEvent,
        val id: Int = Random.nextInt(),
    ) : EventLogEvent()
}

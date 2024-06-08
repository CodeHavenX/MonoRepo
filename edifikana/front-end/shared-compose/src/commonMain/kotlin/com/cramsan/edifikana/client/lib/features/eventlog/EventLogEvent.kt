package com.cramsan.edifikana.client.lib.features.eventlog

import com.cramsan.edifikana.client.lib.features.main.MainActivityEvent
import kotlin.random.Random

sealed class EventLogEvent {
    data object Noop : EventLogEvent()

    data class TriggerMainActivityEvent(
        val mainActivityEvent: MainActivityEvent,
        val id: Int = Random.nextInt(),
    ) : EventLogEvent()
}

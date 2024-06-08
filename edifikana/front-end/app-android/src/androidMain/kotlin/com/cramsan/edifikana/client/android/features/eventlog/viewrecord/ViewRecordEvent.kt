package com.cramsan.edifikana.client.android.features.eventlog.viewrecord

import com.cramsan.edifikana.client.lib.features.main.MainActivityEvent
import kotlin.random.Random

sealed class ViewRecordEvent {
    data object Noop : ViewRecordEvent()

    data class TriggerMainActivityEvent(
        val mainActivityEvent: MainActivityEvent,
        val id: Int = Random.nextInt(),
    ) : ViewRecordEvent()
}

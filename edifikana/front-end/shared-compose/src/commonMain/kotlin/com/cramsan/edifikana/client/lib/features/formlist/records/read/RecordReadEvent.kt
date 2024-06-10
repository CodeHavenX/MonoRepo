package com.cramsan.edifikana.client.lib.features.formlist.records.read

import com.cramsan.edifikana.client.lib.features.main.MainActivityEvent
import kotlin.random.Random

sealed class RecordReadEvent {
    data object Noop : RecordReadEvent()

    data class TriggerMainActivityEvent(
        val mainActivityEvent: MainActivityEvent,
        val id: Int = Random.nextInt(),
    ) : RecordReadEvent()
}

package com.cramsan.edifikana.client.android.features.formlist.records

import com.cramsan.edifikana.client.lib.features.main.MainActivityEvent
import kotlin.random.Random

sealed class RecordsEvent {
    data object Noop : RecordsEvent()

    data class TriggerMainActivityEvent(
        val mainActivityEvent: MainActivityEvent,
        val id: Int = Random.nextInt(),
    ) : RecordsEvent()
}

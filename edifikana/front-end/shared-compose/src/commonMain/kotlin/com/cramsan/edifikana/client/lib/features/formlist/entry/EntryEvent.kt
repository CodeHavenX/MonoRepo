package com.cramsan.edifikana.client.lib.features.formlist.entry

import com.cramsan.edifikana.client.lib.features.main.MainActivityEvent
import kotlin.random.Random

sealed class EntryEvent {
    data object Noop : EntryEvent()

    data class TriggerMainActivityEvent(
        val mainActivityEvent: MainActivityEvent,
        val id: Int = Random.nextInt(),
    ) : EntryEvent()
}

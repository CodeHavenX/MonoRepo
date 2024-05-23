package com.cramsan.edifikana.client.android.features.formlist.entry

import com.cramsan.edifikana.client.android.features.main.MainActivityEvent
import kotlin.random.Random

sealed class EntryEvent {
    data object Noop : EntryEvent()

    data class TriggerMainActivityEvent(
        val mainActivityEvent: MainActivityEvent,
        val id: Int = Random.nextInt(),
    ) : EntryEvent()
}

package com.cramsan.edifikana.client.android.features.timecard

import com.cramsan.edifikana.client.lib.features.main.MainActivityEvent
import kotlin.random.Random

sealed class TimeCardEvent {
    data object Noop : TimeCardEvent()

    data class TriggerMainActivityEvent(
        val mainActivityEvent: MainActivityEvent,
        val id: Int = Random.nextInt(),
    ) : TimeCardEvent()
}

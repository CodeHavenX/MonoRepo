package com.cramsan.edifikana.client.lib.features.timecard.viewemployee

import com.cramsan.edifikana.client.lib.features.main.MainActivityEvent
import kotlin.random.Random

sealed class ViewEmployeeEvent {
    data object Noop : ViewEmployeeEvent()

    data class TriggerMainActivityEvent(
        val mainActivityEvent: MainActivityEvent,
        val id: Int = Random.nextInt(),
    ) : ViewEmployeeEvent()
}

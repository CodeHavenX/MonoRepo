package com.cramsan.edifikana.client.android.features.timecard.viewemployee

import com.cramsan.edifikana.client.android.features.main.MainActivityEvent
import kotlin.random.Random

sealed class ViewEmployeeEvent {
    data object Noop : ViewEmployeeEvent()

    data class TriggerMainActivityEvent(
        val mainActivityEvent: MainActivityEvent,
        val id: Int = Random.nextInt(),
    ) : ViewEmployeeEvent()
}

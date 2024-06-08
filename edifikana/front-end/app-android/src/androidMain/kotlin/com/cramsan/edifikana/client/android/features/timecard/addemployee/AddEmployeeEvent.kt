package com.cramsan.edifikana.client.android.features.timecard.addemployee

import com.cramsan.edifikana.client.lib.features.main.MainActivityEvent
import kotlin.random.Random

sealed class AddEmployeeEvent {
    data object Noop : AddEmployeeEvent()

    data class TriggerMainActivityEvent(
        val mainActivityEvent: MainActivityEvent,
        val id: Int = Random.nextInt(),
    ) : AddEmployeeEvent()
}

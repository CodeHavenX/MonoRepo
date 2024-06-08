package com.cramsan.edifikana.client.android.features.timecard.employeelist

import com.cramsan.edifikana.client.lib.features.main.MainActivityEvent
import kotlin.random.Random

sealed class EmployeeListEvent {
    data object Noop : EmployeeListEvent()

    data class TriggerMainActivityEvent(
        val mainActivityEvent: MainActivityEvent,
        val id: Int = Random.nextInt(),
    ) : EmployeeListEvent()
}

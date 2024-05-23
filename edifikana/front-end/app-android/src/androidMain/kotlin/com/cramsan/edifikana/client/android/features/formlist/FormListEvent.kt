package com.cramsan.edifikana.client.android.features.formlist

import com.cramsan.edifikana.client.android.features.main.MainActivityEvent
import kotlin.random.Random

sealed class FormListEvent {
    data object Noop : FormListEvent()

    data class TriggerMainActivityEvent(
        val mainActivityEvent: MainActivityEvent,
        val id: Int = Random.nextInt(),
    ) : FormListEvent()
}

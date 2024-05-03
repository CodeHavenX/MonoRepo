package com.cramsan.edifikana.client.android.screens.eventlog.add

import android.net.Uri
import com.cramsan.edifikana.client.android.MainActivityEvents
import kotlin.random.Random

sealed class EventLogAddRecordUIEvent {
    data object Noop : EventLogAddRecordUIEvent()

    data object OnAddCompleted : EventLogAddRecordUIEvent()
}

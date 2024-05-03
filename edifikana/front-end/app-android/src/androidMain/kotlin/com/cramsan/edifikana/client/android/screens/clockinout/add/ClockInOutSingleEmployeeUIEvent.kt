package com.cramsan.edifikana.client.android.screens.clockinout.add

sealed class ClockInOutSingleEmployeeUIEvent {
    data object Noop : ClockInOutSingleEmployeeUIEvent()

    data object UploadCompleted : ClockInOutSingleEmployeeUIEvent()
}

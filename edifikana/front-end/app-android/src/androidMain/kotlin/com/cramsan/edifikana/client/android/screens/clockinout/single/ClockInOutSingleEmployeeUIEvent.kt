package com.cramsan.edifikana.client.android.screens.clockinout.single

import android.net.Uri
import kotlin.random.Random

sealed class ClockInOutSingleEmployeeUIEvent(
    open val id: Int = Random.nextInt(),
) {
    data object Noop : ClockInOutSingleEmployeeUIEvent()

    data class OnAddRecordRequested(
        val filename: String,
        override val id: Int = Random.nextInt(),
    ) : ClockInOutSingleEmployeeUIEvent(id)

    data class ShareEvent(
        val text: String,
        val imageUri: Uri? = null,
        override val id: Int = Random.nextInt(),
    ) : ClockInOutSingleEmployeeUIEvent(id)
}

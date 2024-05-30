package com.cramsan.edifikana.client.android.features.camera

import android.net.Uri
import kotlin.random.Random

sealed class CameraEvent {

    data object Noop : CameraEvent()

    data class CancelFlow(
        val id: Int = Random.nextInt(),
    ) : CameraEvent()

    data class CompleteFlow(
        val id: Int = Random.nextInt(),
        val uri: Uri,
    ) : CameraEvent()

    data class OpenSettings(
        val id: Int = Random.nextInt(),
    ) : CameraEvent()
}

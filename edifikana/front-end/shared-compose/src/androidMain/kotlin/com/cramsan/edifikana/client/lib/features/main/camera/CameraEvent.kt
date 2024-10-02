package com.cramsan.edifikana.client.lib.features.main.camera

import android.net.Uri
import kotlin.random.Random

/**
 * Camera event.
 */
sealed class CameraEvent {

    /**
     * No operation.
     */
    data object Noop : CameraEvent()

    /**
     * Cancel flow.
     */
    data class CancelFlow(
        val id: Int = Random.nextInt(),
    ) : CameraEvent()

    /**
     * Complete flow.
     */
    data class CompleteFlow(
        val id: Int = Random.nextInt(),
        val uri: Uri,
    ) : CameraEvent()

    /**
     * Open settings.
     */
    data class OpenSettings(
        val id: Int = Random.nextInt(),
    ) : CameraEvent()
}

package com.cramsan.edifikana.client.lib.features.main.camera

import android.net.Uri

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
    data object CancelFlow : CameraEvent()

    /**
     * Complete flow.
     */
    data class CompleteFlow(
        val uri: Uri,
    ) : CameraEvent()

    /**
     * Open settings.
     */
    data object OpenSettings : CameraEvent()
}

package com.cramsan.edifikana.client.lib.features

import androidx.compose.material3.SnackbarResult
import com.cramsan.framework.core.CoreUri

/**
 * Delegated events that can be triggered in the application. These events
 * are intended to be observed within a feature screen to be able to handle
 * the event.
 */
sealed class EdifikanaApplicationDelegatedEvent {

    /**
     * Handle received image.
     */
    data class HandleReceivedImage(
        val uri: CoreUri,
    ) : EdifikanaApplicationDelegatedEvent()

    /**
     * Handle received images.
     */
    data class HandleReceivedImages(
        val uris: List<CoreUri>,
    ) : EdifikanaApplicationDelegatedEvent()

    /**
     * Handle snackbar result.
     */
    data class HandleSnackbarResult(
        val result: SnackbarResult,
    ) : EdifikanaApplicationDelegatedEvent()
}

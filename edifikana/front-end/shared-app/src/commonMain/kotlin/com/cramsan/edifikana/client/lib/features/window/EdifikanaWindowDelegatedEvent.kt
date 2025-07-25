package com.cramsan.edifikana.client.lib.features.window

import androidx.compose.material3.SnackbarResult
import com.cramsan.framework.core.CoreUri

/**
 * Delegated events that can be triggered in the current window. These events
 * are intended to be observed within a feature screen to be able to handle
 * the event.
 */
sealed class EdifikanaWindowDelegatedEvent {

    /**
     * Handle received image.
     */
    data class HandleReceivedImage(
        val uri: CoreUri,
    ) : EdifikanaWindowDelegatedEvent()

    /**
     * Handle received images.
     */
    data class HandleReceivedImages(
        val uris: List<CoreUri>,
    ) : EdifikanaWindowDelegatedEvent()

    /**
     * Handle snackbar result.
     */
    data class HandleSnackbarResult(
        val result: SnackbarResult,
    ) : EdifikanaWindowDelegatedEvent()
}

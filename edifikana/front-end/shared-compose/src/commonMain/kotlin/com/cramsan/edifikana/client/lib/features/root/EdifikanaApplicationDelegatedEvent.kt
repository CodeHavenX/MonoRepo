package com.cramsan.edifikana.client.lib.features.root

import androidx.compose.material3.SnackbarResult
import com.cramsan.framework.core.CoreUri
import kotlin.random.Random

/**
 * Delegated events that can be triggered in the application. These events
 * are intended to be observed within a feature screen to be able to handle
 * the event.
 */
sealed class EdifikanaApplicationDelegatedEvent {

    /**
     * No operation.
     */
    data object Noop : EdifikanaApplicationDelegatedEvent()

    /**
     * Handle received image.
     */
    data class HandleReceivedImage(
        val uri: CoreUri,
        val id: Int = Random.nextInt(),
    ) : EdifikanaApplicationDelegatedEvent()

    /**
     * Handle received images.
     */
    data class HandleReceivedImages(
        val uris: List<CoreUri>,
        val id: Int = Random.nextInt(),
    ) : EdifikanaApplicationDelegatedEvent()

    /**
     * Handle snackbar result.
     */
    data class HandleSnackbarResult(
        val result: SnackbarResult,
        val id: Int = Random.nextInt(),
    ) : EdifikanaApplicationDelegatedEvent()
}

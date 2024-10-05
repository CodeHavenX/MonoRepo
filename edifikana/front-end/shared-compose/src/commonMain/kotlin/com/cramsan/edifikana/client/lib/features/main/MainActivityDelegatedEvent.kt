package com.cramsan.edifikana.client.lib.features.main

import com.cramsan.framework.core.CoreUri
import kotlin.random.Random

/**
 * Delegated events that can be triggered in the main activity.
 */
sealed class MainActivityDelegatedEvent {

    /**
     * No operation.
     */
    data object Noop : MainActivityDelegatedEvent()

    /**
     * Handle received image.
     */
    data class HandleReceivedImage(
        val uri: CoreUri,
        val id: Int = Random.nextInt(),
    ) : MainActivityDelegatedEvent()

    /**
     * Handle received images.
     */
    data class HandleReceivedImages(
        val uris: List<CoreUri>,
        val id: Int = Random.nextInt(),
    ) : MainActivityDelegatedEvent()
}

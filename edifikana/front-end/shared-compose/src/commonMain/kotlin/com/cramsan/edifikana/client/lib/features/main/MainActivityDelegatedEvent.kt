package com.cramsan.edifikana.client.lib.features.main

import com.cramsan.framework.core.CoreUri
import kotlin.random.Random

sealed class MainActivityDelegatedEvent {
    data object Noop : MainActivityDelegatedEvent()

    data class HandleReceivedImage(
        val uri: CoreUri,
        val id: Int = Random.nextInt(),
    ) : MainActivityDelegatedEvent()

    data class HandleReceivedImages(
        val uris: List<CoreUri>,
        val id: Int = Random.nextInt(),
    ) : MainActivityDelegatedEvent()
}

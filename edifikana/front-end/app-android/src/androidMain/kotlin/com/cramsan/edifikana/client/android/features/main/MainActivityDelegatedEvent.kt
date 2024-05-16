package com.cramsan.edifikana.client.android.features.main

import android.net.Uri
import kotlin.random.Random

sealed class MainActivityDelegatedEvent {
    data object Noop : MainActivityDelegatedEvent()

    data class HandleReceivedImage(
        val uri: Uri,
        val id: Int = Random.nextInt(),
    ) : MainActivityDelegatedEvent()

    data class HandleReceivedImages(
        val uris: List<Uri>,
        val id: Int = Random.nextInt(),
    ) : MainActivityDelegatedEvent()
}

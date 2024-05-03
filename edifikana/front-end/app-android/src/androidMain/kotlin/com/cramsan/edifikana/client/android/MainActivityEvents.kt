package com.cramsan.edifikana.client.android

import android.net.Uri
import kotlin.random.Random

sealed class MainActivityEvents {
    data object Noop : MainActivityEvents()

    data class OnCameraComplete(
        val photoUri: Uri,
        val id: Int = Random.nextInt(),
    ) : MainActivityEvents()

    data class LaunchSignIn(
        val id: Int = Random.nextInt(),
    ) : MainActivityEvents()

    data class ShareToWhatsApp(
        val text: String,
        val imageUri: Uri? = null,
        val id: Int = Random.nextInt(),
    ) : MainActivityEvents()
}
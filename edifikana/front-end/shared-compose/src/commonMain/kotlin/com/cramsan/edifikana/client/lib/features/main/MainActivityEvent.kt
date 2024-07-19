package com.cramsan.edifikana.client.lib.features.main

import com.cramsan.framework.core.CoreUri
import kotlin.random.Random

sealed class MainActivityEvent {
    data object Noop : MainActivityEvent()

    data class OpenCamera(
        val filename: String,
        val id: Int = Random.nextInt(),
    ) : MainActivityEvent()

    data class OpenPhotoPicker(
        val id: Int = Random.nextInt(),
    ) : MainActivityEvent()

    data class ShareContent(
        val text: String,
        val imageUri: CoreUri? = null,
        val id: Int = Random.nextInt(),
    ) : MainActivityEvent()

    data class Navigate(
        val route: String,
        val id: Int = Random.nextInt(),
    ) : MainActivityEvent()

    data class NavigateBack(
        val id: Int = Random.nextInt(),
    ) : MainActivityEvent()

    data class ShowSnackbar(
        val message: String,
        val id: Int = Random.nextInt(),
    ) : MainActivityEvent()

    data class OpenImageExternally(
        val imageUri: CoreUri,
        val id: Int = Random.nextInt(),
    ) : MainActivityEvent()

    data class NavigateToRootPage(
        val route: String,
        val id: Int = Random.nextInt(),
    ) : MainActivityEvent()
}

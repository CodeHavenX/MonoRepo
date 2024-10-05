package com.cramsan.edifikana.client.lib.features.main

import com.cramsan.framework.core.CoreUri
import kotlin.random.Random

/**
 * Events that can be triggered in the main activity.
 */
sealed class MainActivityEvent {

    /**
     * No operation.
     */
    data object Noop : MainActivityEvent()

    /**
     * Open the camera.
     */
    data class OpenCamera(
        val filename: String,
        val id: Int = Random.nextInt(),
    ) : MainActivityEvent()

    /**
     * Open the photo picker.
     */
    data class OpenPhotoPicker(
        val id: Int = Random.nextInt(),
    ) : MainActivityEvent()

    /**
     * Share content.
     */
    data class ShareContent(
        val text: String,
        val imageUri: CoreUri? = null,
        val id: Int = Random.nextInt(),
    ) : MainActivityEvent()

    /**
     * Navigate to a route.
     */
    data class Navigate(
        val route: String,
        val id: Int = Random.nextInt(),
    ) : MainActivityEvent()

    /**
     * Navigate back.
     */
    data class NavigateBack(
        val id: Int = Random.nextInt(),
    ) : MainActivityEvent()

    /**
     * Show a snackbar.
     */
    data class ShowSnackbar(
        val message: String,
        val id: Int = Random.nextInt(),
    ) : MainActivityEvent()

    /**
     * Open an image externally.
     */
    data class OpenImageExternally(
        val imageUri: CoreUri,
        val id: Int = Random.nextInt(),
    ) : MainActivityEvent()

    /**
     * Navigate to the root page.
     */
    data class NavigateToRootPage(
        val route: String,
        val id: Int = Random.nextInt(),
    ) : MainActivityEvent()
}

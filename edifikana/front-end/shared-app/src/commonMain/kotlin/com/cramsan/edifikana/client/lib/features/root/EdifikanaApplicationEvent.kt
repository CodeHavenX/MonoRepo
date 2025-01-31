package com.cramsan.edifikana.client.lib.features.root

import com.cramsan.framework.core.CoreUri
import kotlin.random.Random

/**
 * Events that can be triggered in the whole application. These events are intended to be
 * triggered by a feature screen, and it will be handled by the application.
 */
sealed class EdifikanaApplicationEvent {

    /**
     * No operation.
     */
    data object Noop : EdifikanaApplicationEvent()

    /**
     * Open the camera.
     */
    data class OpenCamera(
        val filename: String,
        val id: Int = Random.nextInt(),
    ) : EdifikanaApplicationEvent()

    /**
     * Open the photo picker.
     */
    data class OpenPhotoPicker(
        val id: Int = Random.nextInt(),
    ) : EdifikanaApplicationEvent()

    /**
     * Share content.
     */
    data class ShareContent(
        val text: String,
        val imageUri: CoreUri? = null,
        val id: Int = Random.nextInt(),
    ) : EdifikanaApplicationEvent()

    /**
     * Navigate to activity.
     */
    data class NavigateToActivity(
        val destination: ActivityRouteDestination,
        val id: Int = Random.nextInt(),
    ) : EdifikanaApplicationEvent()

    /**
     * Close the activity.
     */
    data class CloseActivity(
        val id: Int = Random.nextInt(),
    ) : EdifikanaApplicationEvent()

    /**
     * Show a snackbar.
     */
    data class ShowSnackbar(
        val message: String,
        val id: Int = Random.nextInt(),
    ) : EdifikanaApplicationEvent()

    /**
     * Open an image externally.
     */
    data class OpenImageExternally(
        val imageUri: CoreUri,
        val id: Int = Random.nextInt(),
    ) : EdifikanaApplicationEvent()
}

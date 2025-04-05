package com.cramsan.edifikana.client.lib.features

import com.cramsan.framework.core.CoreUri
import com.cramsan.framework.core.compose.ApplicationViewModelEvent

/**
 * Events that can be triggered in the whole application. These events are intended to be
 * triggered by a feature screen, and it will be handled by the application.
 */
sealed class EdifikanaApplicationEvent : ApplicationViewModelEvent {

    /**
     * Open the camera.
     */
    data class OpenCamera(
        val filename: String,
    ) : EdifikanaApplicationEvent()

    /**
     * Open the photo picker.
     */
    data object OpenPhotoPicker : EdifikanaApplicationEvent()

    /**
     * Share content.
     */
    data class ShareContent(
        val text: String,
        val imageUri: CoreUri? = null,
    ) : EdifikanaApplicationEvent()

    /**
     * Navigate to activity.
     */
    data class NavigateToActivity(
        val destination: ActivityRouteDestination,
        val clearTop: Boolean = false,
        val clearStack: Boolean = false,
    ) : EdifikanaApplicationEvent()

    /**
     * Navigate to destination.
     */
    data class NavigateToScreen(
        val destination: Destination,
    ) : EdifikanaApplicationEvent()

    /**
     * Close the activity.
     */
    data object CloseActivity : EdifikanaApplicationEvent()

    /**
     * Show a snackbar.
     */
    data class ShowSnackbar(
        val message: String,
    ) : EdifikanaApplicationEvent()

    /**
     * Open an image externally.
     */
    data class OpenImageExternally(
        val imageUri: CoreUri,
    ) : EdifikanaApplicationEvent()

    /**
     * Navigate back.
     */
    data object NavigateBack : EdifikanaApplicationEvent()
}

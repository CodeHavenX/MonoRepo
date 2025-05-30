package com.cramsan.edifikana.client.lib.features

import com.cramsan.framework.core.CoreUri
import com.cramsan.framework.core.compose.WindowEvent

/**
 * Events that can be triggered in the whole Window. These events are intended to be
 * triggered by a feature screen, and it will be handled by the window.
 */
sealed class EdifikanaWindowsEvent : WindowEvent {

    /**
     * Open the camera.
     */
    data class OpenCamera(
        val filename: String,
    ) : EdifikanaWindowsEvent()

    /**
     * Open the photo picker.
     */
    data object OpenPhotoPicker : EdifikanaWindowsEvent()

    /**
     * Share content.
     */
    data class ShareContent(
        val text: String,
        val imageUri: CoreUri? = null,
    ) : EdifikanaWindowsEvent()

    /**
     * Navigate to activity.
     */
    data class NavigateToActivity(
        val destination: ActivityRouteDestination,
        val clearTop: Boolean = false,
        val clearStack: Boolean = false,
    ) : EdifikanaWindowsEvent()

    /**
     * Navigate to destination.
     */
    data class NavigateToScreen(
        val destination: Destination,
    ) : EdifikanaWindowsEvent()

    /**
     * Close the activity.
     */
    data object CloseActivity : EdifikanaWindowsEvent()

    /**
     * Show a snackbar.
     */
    data class ShowSnackbar(
        val message: String,
    ) : EdifikanaWindowsEvent()

    /**
     * Open an image externally.
     */
    data class OpenImageExternally(
        val imageUri: CoreUri,
    ) : EdifikanaWindowsEvent()

    /**
     * Navigate back.
     */
    data object NavigateBack : EdifikanaWindowsEvent()
}

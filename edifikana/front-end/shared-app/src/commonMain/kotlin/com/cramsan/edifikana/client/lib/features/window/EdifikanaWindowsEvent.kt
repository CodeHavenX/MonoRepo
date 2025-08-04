package com.cramsan.edifikana.client.lib.features.window

import com.cramsan.framework.core.CoreUri
import com.cramsan.framework.core.compose.WindowEvent
import com.cramsan.framework.core.compose.navigation.Destination

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
     * Navigate to nav graph.
     */
    data class NavigateToNavGraph(
        val destination: EdifikanaNavGraphDestination,
        override val clearTop: Boolean = false,
        override val clearStack: Boolean = false,
    ) : EdifikanaWindowsEvent(), NavigationEvent

    /**
     * Navigate to destination.
     */
    data class NavigateToScreen(
        val destination: Destination,
        override val clearTop: Boolean = false,
        override val clearStack: Boolean = false,
    ) : EdifikanaWindowsEvent(), NavigationEvent

    /**
     * Close the nav graph.
     */
    data object CloseNavGraph : EdifikanaWindowsEvent()

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

/**
 * Interface for navigation events that can clear the top or stack of the navigation.
 */
interface NavigationEvent {
    val clearTop: Boolean
    val clearStack: Boolean
}

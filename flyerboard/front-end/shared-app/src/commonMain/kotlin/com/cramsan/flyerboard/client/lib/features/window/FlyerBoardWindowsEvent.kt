package com.cramsan.flyerboard.client.lib.features.window

import com.cramsan.framework.core.CoreUri
import com.cramsan.framework.core.compose.WindowEvent
import com.cramsan.framework.core.compose.navigation.Destination

/**
 * Events that can be triggered in the whole Window. These events are intended to be
 * triggered by a feature screen, and it will be handled by the window.
 */
sealed class FlyerBoardWindowsEvent : WindowEvent {

    /**
     * Share content.
     */
    data class ShareContent(
        val text: String,
        val imageUri: CoreUri? = null,
    ) : FlyerBoardWindowsEvent()

    /**
     * Navigate to nav graph.
     */
    data class NavigateToNavGraph(
        val destination: FlyerBoardWindowNavGraphDestination,
        override val clearTop: Boolean = false,
        override val clearStack: Boolean = false,
    ) : FlyerBoardWindowsEvent(), NavigationEvent

    /**
     * Navigate to destination.
     */
    data class NavigateToScreen(
        val destination: Destination,
        override val clearTop: Boolean = false,
        override val clearStack: Boolean = false,
    ) : FlyerBoardWindowsEvent(), NavigationEvent

    /**
     * Close the nav graph.
     */
    data object CloseNavGraph : FlyerBoardWindowsEvent()

    /**
     * Show a snackbar.
     */
    data class ShowSnackbar(
        val message: String,
    ) : FlyerBoardWindowsEvent()

    /**
     * Navigate back.
     */
    data object NavigateBack : FlyerBoardWindowsEvent()
}

/**
 * Interface for navigation events that can clear the top or stack of the navigation.
 */
interface NavigationEvent {
    val clearTop: Boolean
    val clearStack: Boolean
}

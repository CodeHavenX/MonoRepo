package com.cramsan.runasimi.client.lib.features.window

import com.cramsan.framework.core.compose.WindowEvent
import com.cramsan.framework.core.compose.navigation.Destination

/**
 * Events that can be triggered in the whole Window. These events are intended to be
 * triggered by a feature screen, and it will be handled by the window.
 */
sealed class RunasimiWindowsEvent : WindowEvent {

    /**
     * Navigate to nav graph.
     */
    data class NavigateToNavGraph(
        val destination: RunasimiNavGraphDestination,
        override val clearTop: Boolean = false,
        override val clearStack: Boolean = false,
    ) : RunasimiWindowsEvent(),
        NavigationEvent

    /**
     * Navigate to destination.
     */
    data class NavigateToScreen(
        val destination: Destination,
        override val clearTop: Boolean = false,
        override val clearStack: Boolean = false,
    ) : RunasimiWindowsEvent(),
        NavigationEvent

    /**
     * Close the nav graph.
     */
    data object CloseNavGraph : RunasimiWindowsEvent()

    /**
     * Show a snackbar.
     */
    data class ShowSnackbar(val message: String) : RunasimiWindowsEvent()

    /**
     * Navigate back.
     */
    data object NavigateBack : RunasimiWindowsEvent()
}

/**
 * Interface for navigation events that can clear the top or stack of the navigation.
 */
interface NavigationEvent {
    val clearTop: Boolean
    val clearStack: Boolean
}

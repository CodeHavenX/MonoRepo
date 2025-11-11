package com.cramsan.templatereplaceme.client.lib.features.window

import com.cramsan.framework.core.CoreUri
import com.cramsan.framework.core.compose.WindowEvent
import com.cramsan.framework.core.compose.navigation.Destination

/**
 * Events that can be triggered in the whole Window. These events are intended to be
 * triggered by a feature screen, and it will be handled by the window.
 */
sealed class TemplateReplaceMeWindowsEvent : WindowEvent {

    /**
     * Share content.
     */
    data class ShareContent(
        val text: String,
        val imageUri: CoreUri? = null,
    ) : TemplateReplaceMeWindowsEvent()

    /**
     * Navigate to nav graph.
     */
    data class NavigateToNavGraph(
        val destination: TemplateReplaceMeWindowNavGraphDestination,
        override val clearTop: Boolean = false,
        override val clearStack: Boolean = false,
    ) : TemplateReplaceMeWindowsEvent(), NavigationEvent

    /**
     * Navigate to destination.
     */
    data class NavigateToScreen(
        val destination: Destination,
        override val clearTop: Boolean = false,
        override val clearStack: Boolean = false,
    ) : TemplateReplaceMeWindowsEvent(), NavigationEvent

    /**
     * Close the nav graph.
     */
    data object CloseNavGraph : TemplateReplaceMeWindowsEvent()

    /**
     * Show a snackbar.
     */
    data class ShowSnackbar(
        val message: String,
    ) : TemplateReplaceMeWindowsEvent()

    /**
     * Navigate back.
     */
    data object NavigateBack : TemplateReplaceMeWindowsEvent()
}

/**
 * Interface for navigation events that can clear the top or stack of the navigation.
 */
interface NavigationEvent {
    val clearTop: Boolean
    val clearStack: Boolean
}

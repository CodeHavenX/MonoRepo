package com.cramsan.edifikana.client.lib.features.home.drawer

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events that can be triggered within the domain of the Management feature.
 *
 * Events are triggered from a ViewModel and are consumed by the UI.
 *
 */
sealed class DrawerEvent : ViewModelEvent {

    /**
     * Trigger closing or opening the navigation drawer.
     */
    data object ToggleDrawer : DrawerEvent()

    /**
     * Trigger closing the navigation drawer.
     */
    data object CloseDrawer : DrawerEvent()
}

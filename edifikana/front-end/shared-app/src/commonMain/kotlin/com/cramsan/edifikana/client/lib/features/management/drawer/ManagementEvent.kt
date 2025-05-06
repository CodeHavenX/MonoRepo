package com.cramsan.edifikana.client.lib.features.management.drawer

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events that can be triggered within the domain of the Management feature.
 *
 * Events are triggered from a ViewModel and are consumed by the UI.
 *
 */
sealed class ManagementEvent : ViewModelEvent {

    /**
     * Trigger closing or opening the navigation drawer.
     */
    data object ToggleDrawer : ManagementEvent()

    /**
     * Trigger closing the navigation drawer.
     */
    data object CloseDrawer : ManagementEvent()
}

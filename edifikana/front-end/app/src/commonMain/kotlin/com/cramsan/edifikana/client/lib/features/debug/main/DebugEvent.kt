package com.cramsan.edifikana.client.lib.features.debug.main

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events that can be triggered within the domain of the Debug feature.
 *
 * Events are triggered from a ViewModel and are consumed by the UI.
 *
 */
sealed class DebugEvent : ViewModelEvent {
    /**
     * Close application event.
     *
     * This is used for debugging purposes to close the application.
     * It is not intended for production use.
     */
    data object CloseApplication : DebugEvent()

    /**
     * Clear preferences event.
     *
     * This is used for debugging purposes to clear the application's preferences.
     * It is not intended for production use.
     */
    data object ClearPreferences : DebugEvent()
}

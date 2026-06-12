package com.cramsan.edifikana.client.lib.features.settings.general

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events for the Settings screen. Kept minimal for now.
 */
sealed interface SettingsEvent : ViewModelEvent {
    /**
     * No-op event used as a placeholder when no action is required.
     */
    data object Noop : SettingsEvent
}

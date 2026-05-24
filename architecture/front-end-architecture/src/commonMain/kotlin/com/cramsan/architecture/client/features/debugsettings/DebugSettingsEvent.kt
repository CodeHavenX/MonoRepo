package com.cramsan.architecture.client.features.debugsettings

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events emitted by [DebugSettingsViewModel].
 */
sealed interface DebugSettingsEvent : ViewModelEvent {
    /**
     * No-op placeholder event.
     */
    data object Noop : DebugSettingsEvent

    /**
     * Requests the UI to show a snackbar with [message].
     *
     * @property message The message to display in the snackbar.
     */
    data class ShowSnackbar(val message: String) : DebugSettingsEvent
}

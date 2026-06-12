package com.cramsan.edifikana.client.lib.features.home.appshell

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events that can be triggered within the domain of the App Shell feature.
 *
 * Events are triggered from a ViewModel and are consumed by the UI.
 */
sealed class AppShellEvent : ViewModelEvent {
    /**
     * No-op event placeholder.
     */
    data object Noop : AppShellEvent()
}

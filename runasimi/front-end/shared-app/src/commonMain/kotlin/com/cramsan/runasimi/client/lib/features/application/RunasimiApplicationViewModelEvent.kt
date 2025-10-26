package com.cramsan.runasimi.client.lib.features.application

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events that can be triggered in the whole application. These events are intended to be
 * triggered by a feature screen, and it will be handled by the application.
 */
sealed class RunasimiApplicationViewModelEvent : ViewModelEvent {

    /**
     * Noop event.
     */
    data object Noop : RunasimiApplicationViewModelEvent()
}

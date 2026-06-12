package com.cramsan.runasimi.client.lib.features.main.verbs

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events that can be triggered within the domain of the Verbs feature.
 *
 * Events are triggered from a ViewModel and are consumed by the UI.
 *
 */
sealed class VerbsEvent : ViewModelEvent {
    /**
     * No operation.
     */
    data object Noop : VerbsEvent()
}

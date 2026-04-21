package com.cramsan.flyerboard.client.lib.features.main.flyer_edit

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events that can be triggered within the domain of the Flyer Edit feature.
 */
sealed class FlyerEditEvent : ViewModelEvent {
    /**
     * No operation.
     */
    data object Noop : FlyerEditEvent()
}

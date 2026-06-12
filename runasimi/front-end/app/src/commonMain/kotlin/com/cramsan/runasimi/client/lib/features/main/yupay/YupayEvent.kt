package com.cramsan.runasimi.client.lib.features.main.yupay

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events that can be triggered within the domain of the Yupay feature.
 *
 * Events are triggered from a ViewModel and are consumed by the UI.
 *
 */
sealed class YupayEvent : ViewModelEvent {
    /**
     * No operation.
     */
    data object Noop : YupayEvent()
}

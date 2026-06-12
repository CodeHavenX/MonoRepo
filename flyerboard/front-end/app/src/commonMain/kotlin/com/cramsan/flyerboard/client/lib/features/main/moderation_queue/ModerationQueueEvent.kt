package com.cramsan.flyerboard.client.lib.features.main.moderation_queue

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events that can be triggered within the domain of the Moderation Queue feature.
 */
sealed class ModerationQueueEvent : ViewModelEvent {
    /**
     * No operation.
     */
    data object Noop : ModerationQueueEvent()
}

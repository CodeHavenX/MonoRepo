package com.cramsan.runasimi.client.lib.features.main.questions

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events that can be triggered within the domain of the Questions feature.
 */
sealed class QuestionsEvent : ViewModelEvent {

    /**
     * No operation.
     */
    data object Noop : QuestionsEvent()
}

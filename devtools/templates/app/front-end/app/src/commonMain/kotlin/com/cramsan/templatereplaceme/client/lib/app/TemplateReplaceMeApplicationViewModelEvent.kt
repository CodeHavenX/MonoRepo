package com.cramsan.templatereplaceme.client.lib.app

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events for the TemplateReplaceMeApplicationViewModel.
 */
sealed class TemplateReplaceMeApplicationViewModelEvent : ViewModelEvent {
    /**
     * Noop event.
     */
    data object Noop : TemplateReplaceMeApplicationViewModelEvent()
}

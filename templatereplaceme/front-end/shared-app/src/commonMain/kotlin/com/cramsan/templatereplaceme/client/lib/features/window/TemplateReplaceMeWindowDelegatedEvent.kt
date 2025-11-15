package com.cramsan.templatereplaceme.client.lib.features.window

import androidx.compose.material3.SnackbarResult
import com.cramsan.framework.core.compose.DelegatedWindowEvent

/**
 * Delegated events that can be triggered in the current window. These events
 * are intended to be observed within a feature screen to be able to handle
 * the event.
 */
sealed class TemplateReplaceMeWindowDelegatedEvent : DelegatedWindowEvent {

    /**
     * Handle the result of a snackbar.
     */
    data class HandleSnackbarResult(
        val result: SnackbarResult,
    ) : TemplateReplaceMeWindowDelegatedEvent()
}

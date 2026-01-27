package com.cramsan.runasimi.client.lib.features.window

import androidx.compose.material3.SnackbarResult

/**
 * Delegated events that can be triggered in the current window. These events
 * are intended to be observed within a feature screen to be able to handle
 * the event.
 */
sealed class RunasimiWindowDelegatedEvent {

    /**
     * Handle snackbar result.
     */
    data class HandleSnackbarResult(val result: SnackbarResult) : RunasimiWindowDelegatedEvent()
}

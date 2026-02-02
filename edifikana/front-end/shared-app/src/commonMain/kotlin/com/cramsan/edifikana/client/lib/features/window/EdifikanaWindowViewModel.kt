package com.cramsan.edifikana.client.lib.features.window

import androidx.compose.material3.SnackbarResult
import com.cramsan.framework.core.CoreUri
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.EventEmitter
import com.cramsan.framework.core.compose.EventReceiver
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.core.compose.WindowEvent
import com.cramsan.framework.logging.logI
import kotlinx.coroutines.launch

/**
 * View model for the entire window.
 */
class EdifikanaWindowViewModel(
    dependencies: ViewModelDependencies,
    private val windowEventEmitter: EventEmitter<WindowEvent>,
    private val delegatedEventsEmitter: EventEmitter<EdifikanaWindowDelegatedEvent>,
) : BaseViewModel<EdifikanaWindowViewModelEvent, EdifikanaWindowUIState>(
    dependencies,
    EdifikanaWindowUIState,
    TAG
) {

    /**
     * Initialize the view model and all required state for the entire application.
     */
    fun initialize() {
        // Observe and re-emit window events
        viewModelScope.launch {
            windowEventEmitter.events.collect { event ->
                logI(TAG, "Window event received: $event")
                emitEvent(
                    EdifikanaWindowViewModelEvent.EdifikanaWindowEventWrapper(
                        event as EdifikanaWindowsEvent
                    )
                )
            }
        }
    }

    /**
     * Handle received image.
     */
    fun handleReceivedImage(uri: CoreUri?) = viewModelScope.launch {
        if (uri == null) {
            logI(TAG, "Uri was null.")
        } else {
            logI(TAG, "Uri was received: $uri")
            val event = EdifikanaWindowDelegatedEvent.HandleReceivedImage(uri)
            emitEvent(EdifikanaWindowViewModelEvent.EdifikanaDelegatedEventWrapper(event))
        }
    }

    /**
     * Handle received images.
     */
    fun handleReceivedImages(uris: List<CoreUri>) = viewModelScope.launch {
        if (uris.isEmpty()) {
            logI(TAG, "Uri list is empty.")
        } else {
            logI(TAG, "Uri list received with ${uris.count()} elements.")
            val event = EdifikanaWindowDelegatedEvent.HandleReceivedImages(uris)
            emitEvent(EdifikanaWindowViewModelEvent.EdifikanaDelegatedEventWrapper(event))
        }
    }

    /**
     * Handle snackbar result and emits it as a delegated event. Any observer can then consume this event.
     */
    fun handleSnackbarResult(result: SnackbarResult) {
        viewModelScope.launch {
            logI(TAG, "Result from snackbar: $result")
            val event = EdifikanaWindowDelegatedEvent.HandleSnackbarResult(result)
            emitEvent(EdifikanaWindowViewModelEvent.EdifikanaDelegatedEventWrapper(event))
        }
    }

    companion object {
        private const val TAG = "EdifikanaWindowViewModel"
    }
}

package com.cramsan.edifikana.client.lib.features

import androidx.compose.material3.SnackbarResult
import com.cramsan.framework.core.CoreUri
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.EventBus
import com.cramsan.framework.core.compose.EventEmitter
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
    private val delegatedEvents: EventBus<EdifikanaWindowDelegatedEvent>,
) : BaseViewModel<EdifikanaWindowViewModelEvent, EdifikanaWindowUIState>(
    dependencies,
    EdifikanaWindowUIState,
    TAG
) {

    init {
        viewModelScope.launch {
            delegatedEvents.events.collect {

                logI(TAG, "Delegated event received: $it")
            }
        }

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
            delegatedEvents.emit(EdifikanaWindowDelegatedEvent.HandleReceivedImage(uri))
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
            delegatedEvents.emit(EdifikanaWindowDelegatedEvent.HandleReceivedImages(uris))
        }
    }

    /**
     * Handle snackbar result and emits it as a delegated event. Any observer can then consume this event.
     */
    fun handleSnackbarResult(result: SnackbarResult) {
        viewModelScope.launch {
            logI(TAG, "Result from snackbar: $result")
            delegatedEvents.emit(EdifikanaWindowDelegatedEvent.HandleSnackbarResult(result))
        }
    }

    companion object {
        private const val TAG = "EdifikanaWindowViewModel"
    }
}

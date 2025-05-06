package com.cramsan.edifikana.client.lib.features

import androidx.compose.material3.SnackbarResult
import com.cramsan.edifikana.client.lib.init.Initializer
import com.cramsan.framework.core.CoreUri
import com.cramsan.framework.core.compose.ApplicationEventEmitter
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.logging.logI
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

/**
 * View model for the entire application.
 */
class EdifikanaApplicationViewModel(
    private val initHandler: Initializer,
    dependencies: ViewModelDependencies,
    private val applicationEventEmitter: ApplicationEventEmitter,
) : BaseViewModel<EdifikanaApplicationViewModelEvent, EdifikanaApplicationUIState>(
    dependencies,
    EdifikanaApplicationUIState,
    TAG
) {

    private val _delegatedEvents = MutableSharedFlow<EdifikanaApplicationDelegatedEvent>()
    val delegatedEvents: SharedFlow<EdifikanaApplicationDelegatedEvent> = _delegatedEvents

    init {
        viewModelScope.launch {
            initHandler.startStep()
        }

        viewModelScope.launch {
            delegatedEvents.collect {
                logI(TAG, "Delegated event received: $it")
            }
        }

        viewModelScope.launch {
            applicationEventEmitter.events.collect { event ->
                logI(TAG, "Application event received: $event")
                emitEvent(
                    EdifikanaApplicationViewModelEvent.EdifikanaApplicationEventWrapper(
                        event as EdifikanaApplicationEvent
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
            _delegatedEvents.emit(EdifikanaApplicationDelegatedEvent.HandleReceivedImage(uri))
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
            _delegatedEvents.emit(EdifikanaApplicationDelegatedEvent.HandleReceivedImages(uris))
        }
    }

    /**
     * Handle snackbar result and emits it as a delegated event. Any observer can then consume this event.
     */
    fun handleSnackbarResult(result: SnackbarResult) {
        viewModelScope.launch {
            logI(TAG, "Result from snackbar: $result")
            _delegatedEvents.emit(EdifikanaApplicationDelegatedEvent.HandleSnackbarResult(result))
        }
    }

    companion object {
        private const val TAG = "EdifikanaApplicationViewModel"
    }
}

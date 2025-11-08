package com.cramsan.templatereplaceme.client.lib.features.window

import androidx.compose.material3.SnackbarResult
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
class TemplateReplaceMeWindowViewModel(
    dependencies: ViewModelDependencies,
    private val windowEventEmitter: EventEmitter<WindowEvent>,
    private val delegatedEvents: EventReceiver<TemplateReplaceMeWindowDelegatedEvent>,
) : BaseViewModel<TemplateReplaceMeWindowViewModelEvent, TemplateReplaceMeWindowUIState>(
    dependencies,
    TemplateReplaceMeWindowUIState,
    TAG
) {

    init {
        viewModelScope.launch {
            windowEventEmitter.events.collect { event ->
                logI(TAG, "Window event received: $event")
                emitEvent(
                    TemplateReplaceMeWindowViewModelEvent.TemplateReplaceMeWindowEventWrapper(
                        event as TemplateReplaceMeWindowsEvent
                    )
                )
            }
        }
    }

    /**
     * Handle snackbar result and emits it as a delegated event. Any observer can then consume this event.
     */
    fun handleSnackbarResult(result: SnackbarResult) {
        viewModelScope.launch {
            logI(TAG, "Result from snackbar: $result")
            delegatedEvents.push(TemplateReplaceMeWindowDelegatedEvent.HandleSnackbarResult(result))
        }
    }

    companion object {
        private const val TAG = "TemplateReplaceMeWindowViewModel"
    }
}

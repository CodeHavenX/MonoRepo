package com.cramsan.framework.sample.shared.features

import com.cramsan.framework.annotations.FrontendViewModel
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.EventEmitter
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.core.compose.WindowEvent
import com.cramsan.framework.core.compose.navigation.NavigateBackWithResult
import com.cramsan.framework.sample.shared.init.Initializer
import kotlinx.coroutines.launch

/**
 * View model for the entire application.
 */
@FrontendViewModel
class ApplicationViewModel(
    private val initHandler: Initializer,
    dependencies: ViewModelDependencies,
    private val eventEmitter: EventEmitter<WindowEvent>,
) : BaseViewModel<SampleApplicationViewModelEvent, ApplicationUIState>(
    dependencies,
    ApplicationUIState,
    TAG,
) {
    init {
        viewModelCoroutineScope.launch {
            initHandler.startStep()
        }
        viewModelCoroutineScope.launch {
            eventEmitter.events.collect { event ->
                val vmEvent =
                    when (event) {
                        is NavigateBackWithResult -> SampleApplicationViewModelEvent.NavBackWithResult(event)
                        is SampleWindowEvent -> SampleApplicationViewModelEvent.SampleApplicationEventWrapper(event)
                        else -> null
                    }
                vmEvent?.let { emitEvent(it) }
            }
        }
    }

    /**
     * Execute application-wide events. The implementation that handles the events is up to the
     * consumer of the view model's events.
     */
    fun executeEvent(event: SampleApplicationViewModelEvent) =
        viewModelCoroutineScope.launch {
            emitEvent(event)
        }

    companion object {
        private const val TAG = "ApplicationViewModel"
    }
}

package com.cramsan.framework.sample.shared.features

import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.EventEmitter
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.sample.shared.init.Initializer
import kotlinx.coroutines.launch

/**
 * View model for the entire application.
 */
class ApplicationViewModel(
    private val initHandler: Initializer,
    dependencies: ViewModelDependencies,
    private val eventEmitter: EventEmitter<SampleWindowEvent>,
) : BaseViewModel<SampleApplicationViewModelEvent, ApplicationUIState>(
    dependencies,
    ApplicationUIState,
    TAG,
) {

    init {
        viewModelScope.launch {
            initHandler.startStep()
        }
        viewModelScope.launch {
            eventEmitter.events.collect { event ->
                emitEvent(SampleApplicationViewModelEvent.SampleApplicationEventWrapper(event))
            }
        }
    }

    /**
     * Execute application-wide events. The implementation that handles the events is up to the
     * consumer of the view model's events.
     */
    fun executeEvent(event: SampleApplicationViewModelEvent) = viewModelScope.launch {
        emitEvent(event)
    }

    companion object {
        private const val TAG = "ApplicationViewModel"
    }
}

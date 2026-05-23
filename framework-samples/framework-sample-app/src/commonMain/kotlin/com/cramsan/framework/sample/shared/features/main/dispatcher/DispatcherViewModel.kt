package com.cramsan.framework.sample.shared.features.main.dispatcher

import com.cramsan.framework.annotations.FrontendViewModel
import com.cramsan.framework.core.DispatcherProvider
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.sample.shared.features.SampleWindowEvent
import kotlinx.coroutines.launch

/**
 * ViewModel for the DispatcherProvider screen.
 */
@FrontendViewModel
class DispatcherViewModel(dependencies: ViewModelDependencies, private val dispatcherProvider: DispatcherProvider) :
    BaseViewModel<DispatcherEvent, DispatcherUIState>(
        dependencies,
        DispatcherUIState.Initial,
        TAG,
    ) {
    /**
     * Query ioDispatcher() and update the UI state with the dispatcher's string representation.
     */
    fun queryIoDispatcher() {
        val info = dispatcherProvider.ioDispatcher().toString()
        viewModelCoroutineScope.launch {
            updateUiState { it.copy(ioDispatcherInfo = info) }
        }
    }

    /**
     * Query uiDispatcher() and update the UI state with the dispatcher's string representation.
     */
    fun queryUiDispatcher() {
        val info = dispatcherProvider.uiDispatcher().toString()
        viewModelCoroutineScope.launch {
            updateUiState { it.copy(uiDispatcherInfo = info) }
        }
    }

    /**
     * Navigate back to the main menu.
     */
    fun navigateBack() {
        viewModelCoroutineScope.launch {
            emitWindowEvent(SampleWindowEvent.NavigateBack)
        }
    }

    companion object {
        private const val TAG = "DispatcherViewModel"
    }
}

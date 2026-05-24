package com.cramsan.framework.sample.shared.features.main.crashhandler

import com.cramsan.framework.annotations.FrontendViewModel
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.crashhandler.CrashHandler
import com.cramsan.framework.sample.shared.features.SampleWindowEvent
import kotlinx.coroutines.launch

/**
 * ViewModel for the CrashHandler screen.
 */
@FrontendViewModel
class CrashHandlerViewModel(dependencies: ViewModelDependencies, private val crashHandler: CrashHandler) :
    BaseViewModel<CrashHandlerEvent, CrashHandlerUIState>(
        dependencies,
        CrashHandlerUIState.Initial,
        TAG,
    ) {
    /**
     * Call CrashHandler.initialize() and update the UI state with confirmation.
     */
    fun initialize() {
        crashHandler.initialize()
        viewModelCoroutineScope.launch {
            updateUiState { it.copy(isInitialized = true) }
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
        private const val TAG = "CrashHandlerViewModel"
    }
}

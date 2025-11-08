package com.cramsan.templatereplaceme.client.lib.features.application

import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.templatereplaceme.client.lib.init.Initializer
import kotlinx.coroutines.launch

/**
 * View model for the entire application.
 */
class TemplateReplaceMeApplicationViewModel(
    private val initHandler: Initializer,
    dependencies: ViewModelDependencies,
) : BaseViewModel<TemplateReplaceMeApplicationViewModelEvent, TemplateReplaceMeApplicationUIState>(
    dependencies,
    TemplateReplaceMeApplicationUIState(),
    TAG
) {
    /**
     * Initialize the view model and all required state for the entire application.
     */
    fun initialize() {
        viewModelScope.launch {
            viewModelScope.launch {
                initHandler.startStep()
            }
        }
    }

    /**
     * Set whether to show the debug window.
     */
    fun setShowDebugWindow(show: Boolean) {
        viewModelScope.launch {
            updateUiState {
                it.copy(showDebugWindow = show)
            }
        }
    }

    companion object {
        private const val TAG = "TemplateReplaceMeApplicationViewModel"
    }
}

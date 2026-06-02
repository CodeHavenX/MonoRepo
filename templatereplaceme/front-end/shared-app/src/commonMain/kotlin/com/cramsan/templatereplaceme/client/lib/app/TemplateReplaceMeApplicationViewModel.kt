package com.cramsan.templatereplaceme.client.lib.app

import com.cramsan.framework.annotations.FrontendViewModel
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.templatereplaceme.client.lib.managers.InitializerManager
import kotlinx.coroutines.launch

/**
 * View model for the entire application.
 */
@FrontendViewModel
class TemplateReplaceMeApplicationViewModel(
    private val initHandler: InitializerManager,
    dependencies: ViewModelDependencies,
) : BaseViewModel<TemplateReplaceMeApplicationViewModelEvent, TemplateReplaceMeApplicationUIState>(
    dependencies,
    TemplateReplaceMeApplicationUIState.Initial,
    TAG,
) {
    /**
     * Initialize the view model and all required state for the entire application.
     */
    fun initialize() {
        initHandler.startStep()
    }

    /**
     * Set whether to show the debug window.
     */
    fun setShowDebugWindow(show: Boolean) {
        viewModelCoroutineScope.launch {
            updateUiState {
                it.copy(showDebugWindow = show)
            }
        }
    }

    companion object {
        private const val TAG = "TemplateReplaceMeApplicationViewModel"
    }
}

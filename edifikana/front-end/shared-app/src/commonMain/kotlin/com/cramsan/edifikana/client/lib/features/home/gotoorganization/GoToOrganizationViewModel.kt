package com.cramsan.edifikana.client.lib.features.home.gotoorganization

import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import kotlinx.coroutines.launch

/**
 * ViewModel for the GoToOrganization screen.
 **/
class GoToOrganizationViewModel(dependencies: ViewModelDependencies) :
    BaseViewModel<GoToOrganizationEvent, GoToOrganizationUIState>(
        dependencies,
        GoToOrganizationUIState,
        TAG,
    ) {

    /**
     * Trigger the back event.
     */
    fun onBackSelected() {
        viewModelScope.launch {
            emitWindowEvent(EdifikanaWindowsEvent.NavigateBack)
        }
    }

    companion object {
        private const val TAG = "GoToOrganizationViewModel"
    }
}

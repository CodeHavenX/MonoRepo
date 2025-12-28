package com.cramsan.edifikana.client.lib.features.home.employeeoverview

import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import kotlinx.coroutines.launch

/**
 * ViewModel for the EmployeeOverview screen.
 **/
class EmployeeOverviewViewModel(
    dependencies: ViewModelDependencies,
) : BaseViewModel<EmployeeOverviewEvent, EmployeeOverviewUIState>(
    dependencies,
    EmployeeOverviewUIState.Initial,
    TAG,
) {
    /**
     * Initialize the ViewModel.
     */
    fun initialize() {
        viewModelScope.launch {
            updateUiState {
                it.copy(isLoading = false)
            }
        }
    }

    companion object {
        private const val TAG = "EmployeeOverviewViewModel"
    }
}

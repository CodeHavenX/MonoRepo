package com.cramsan.edifikana.client.lib.features.admin.staff

import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationEvent
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import kotlinx.coroutines.launch

/**
 * ViewModel for the Staff screen.
 **/
class StaffViewModel(
    dependencies: ViewModelDependencies,
) : BaseViewModel<StaffEvent, StaffUIState>(
    dependencies,
    StaffUIState.Initial,
    TAG,
) {

    /**
     * Load the staff with the given [staffId].
     */
    fun loadStaff(staffId: StaffId) {
        updateUiState {
            it.copy(isLoading = true)
        }
        viewModelScope.launch {
            updateUiState {
                it.copy(
                    title = staffId.staffId,
                    isLoading = false,
                )
            }
        }
    }

    /**
     * Trigger the back event.
     */
    fun onBackSelected() {
        viewModelScope.launch {
            emitApplicationEvent(EdifikanaApplicationEvent.NavigateBack)
        }
    }

    companion object {
        private const val TAG = "StaffViewModel"
    }
}

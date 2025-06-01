package com.cramsan.edifikana.client.lib.features.admin.staff

import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.StaffManager
import com.cramsan.edifikana.client.lib.models.fullName
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import kotlinx.coroutines.launch

/**
 * ViewModel for the Staff screen.
 **/
class StaffViewModel(
    dependencies: ViewModelDependencies,
    private val staffManager: StaffManager,
) : BaseViewModel<StaffEvent, StaffUIState>(
    dependencies,
    StaffUIState.Initial,
    TAG,
) {

    /**
     * Load the staff with the given [staffId].
     */
    fun loadStaff(staffId: StaffId) {
        viewModelScope.launch {
            val staffResult = staffManager.getStaff(staffId)

            if (staffResult.isFailure) {
                updateUiState {
                    it.copy(
                        title = "",
                        isLoading = false,
                    )
                }
                return@launch
            }
            val staff = staffResult.getOrThrow()
            updateUiState {
                it.copy(
                    title = staff.fullName(),
                    isLoading = false,
                    idType = staff.idType,
                    firstName = staff.name,
                    lastName = staff.lastName,
                    role = staff.role,
                )
            }
        }
    }

    /**
     * Trigger the back event.
     */
    fun onBackSelected() {
        viewModelScope.launch {
            emitWindowEvent(EdifikanaWindowsEvent.NavigateBack)
        }
    }

    companion object {
        private const val TAG = "StaffViewModel"
    }
}

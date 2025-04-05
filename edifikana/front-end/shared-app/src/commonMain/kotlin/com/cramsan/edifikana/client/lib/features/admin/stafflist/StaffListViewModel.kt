package com.cramsan.edifikana.client.lib.features.admin.stafflist

import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationEvent
import com.cramsan.edifikana.client.lib.features.management.ManagementDestination
import com.cramsan.edifikana.client.lib.managers.StaffManager
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import kotlinx.coroutines.launch

/**
 * ViewModel for the StaffList screen.
 **/
class StaffListViewModel(
    dependencies: ViewModelDependencies,
    private val staffManager: StaffManager,
) : BaseViewModel<StaffListEvent, StaffListUIState>(
    dependencies,
    StaffListUIState.Initial,
    TAG,
) {

    /**
     * Load the staff list.
     */
    fun loadStaffList() {
        viewModelScope.launch {
            val staffList = staffManager.getStaffList().onFailure {
                updateUiState {
                    it.copy(isLoading = false)
                }
                return@launch
            }
                .getOrThrow()
                .map { it.name }

            updateUiState {
                it.copy(isLoading = false, staffList = staffList)
            }
        }
    }

    /**
     * Navigate to the AddPrimaryStaff screen.
     */
    fun navigateToAddPrimaryStaff() {
        viewModelScope.launch {
            emitApplicationEvent(
                EdifikanaApplicationEvent.NavigateToScreen(ManagementDestination.AddPrimaryStaffManagementDestination)
            )
        }
    }

    /**
     * Navigate to the AddSecondaryStaff screen.
     */
    fun navigateToAddSecondaryStaff() {
        viewModelScope.launch {
            emitApplicationEvent(
                EdifikanaApplicationEvent.NavigateToScreen(ManagementDestination.AddSecondaryStaffManagementDestination)
            )
        }
    }

    /**
     * Navigate to the staff screen.
     */
    fun navigateToStaff(staffId: StaffId) {
        viewModelScope.launch {
            emitApplicationEvent(
                EdifikanaApplicationEvent.NavigateToScreen(ManagementDestination.StaffDestination(staffId))
            )
        }
    }

    companion object {
        private const val TAG = "StaffListViewModel"
    }
}

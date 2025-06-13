package com.cramsan.edifikana.client.lib.features.management.timecardstafflist

import com.cramsan.edifikana.client.lib.features.management.ManagementDestination
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.StaffManager
import com.cramsan.edifikana.client.lib.models.StaffModel
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
                .map { it.toUIModel() }

            updateUiState {
                it.copy(isLoading = false, staffList = staffList)
            }
        }
    }

    private fun StaffModel.toUIModel(): StaffUIModel {
        return StaffUIModel(
            id = id,
            name = name,
            email = email,
            status = status,
        )
    }

    /**
     * Navigate to the AddPrimaryStaff screen.
     */
    fun navigateToAddPrimaryStaff() {
        viewModelScope.launch {
            emitWindowEvent(
                EdifikanaWindowsEvent.NavigateToScreen(ManagementDestination.AddPrimaryStaffManagementDestination)
            )
        }
    }

    /**
     * Navigate to the AddSecondaryStaff screen.
     */
    fun navigateToAddSecondaryStaff() {
        viewModelScope.launch {
            emitWindowEvent(
                EdifikanaWindowsEvent.NavigateToScreen(ManagementDestination.AddSecondaryStaffManagementDestination)
            )
        }
    }

    /**
     * Navigate to the staff screen.
     */
    fun navigateToStaff(staffId: StaffId) {
        viewModelScope.launch {
            emitWindowEvent(
                EdifikanaWindowsEvent.NavigateToScreen(ManagementDestination.StaffDestination(staffId))
            )
        }
    }

    companion object {
        private const val TAG = "StaffListViewModel"
    }
}

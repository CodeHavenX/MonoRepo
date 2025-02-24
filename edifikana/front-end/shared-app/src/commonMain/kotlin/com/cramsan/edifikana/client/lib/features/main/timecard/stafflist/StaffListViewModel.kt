package com.cramsan.edifikana.client.lib.features.main.timecard.stafflist

import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationEvent
import com.cramsan.edifikana.client.lib.features.admin.AdminDestination
import com.cramsan.edifikana.client.lib.features.main.MainRouteDestination
import com.cramsan.edifikana.client.lib.managers.StaffManager
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import edifikana_lib.Res
import edifikana_lib.title_timecard_staff_list
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

/**
 * Represents the UI state of the Staff List screen.
 */
class StaffListViewModel(
    private val staffManager: StaffManager,
    dependencies: ViewModelDependencies,
) : BaseViewModel<StaffListEvent, StaffListUIState>(dependencies, StaffListUIState.Empty, TAG) {

    /**
     * Load staff members.
     */
    fun loadStaffs() = viewModelScope.launch {
        updateUiState { it.copy(isLoading = true) }
        val result = staffManager.getStaffList()

        if (result.isFailure) {
            val state = StaffListUIState(
                emptyList(),
                false,
                getString(Res.string.title_timecard_staff_list),
            )
            updateUiState { state }
        } else {
            val staffs = result.getOrThrow()
            val state = StaffListUIState(
                staffs.map {
                    it.toUIModel()
                },
                false,
                getString(Res.string.title_timecard_staff_list)
            )
            updateUiState { state }
        }
    }

    /**
     * Navigate to staff member.
     */
    fun navigateToStaff(staffPK: StaffId) = viewModelScope.launch {
        emitEvent(
            StaffListEvent.TriggerEdifikanaApplicationEvent(
                EdifikanaApplicationEvent.NavigateToScreen(
                    MainRouteDestination.TimeCardSingleStaffDestination(staffPK)
                )
            )
        )
    }

    /**
     * Navigate to add staff member.
     */
    fun navigateToAddStaff() = viewModelScope.launch {
        emitEvent(
            StaffListEvent.TriggerEdifikanaApplicationEvent(
                EdifikanaApplicationEvent.NavigateToScreen(
                    AdminDestination.AddSecondaryStaffAdminDestination,
                )
            )
        )
    }

    /**
     * Navigate back.
     */
    fun navigateBack() = viewModelScope.launch {
        emitEvent(
            StaffListEvent.TriggerEdifikanaApplicationEvent(
                EdifikanaApplicationEvent.NavigateBack
            )
        )
    }

    companion object {
        private const val TAG = "StaffListViewModel"
    }
}

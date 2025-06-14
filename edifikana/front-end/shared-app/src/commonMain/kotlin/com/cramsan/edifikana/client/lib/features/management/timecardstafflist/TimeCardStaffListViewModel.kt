package com.cramsan.edifikana.client.lib.features.management.timecardstafflist

import com.cramsan.edifikana.client.lib.features.management.ManagementDestination
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.StaffManager
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.core.compose.resources.StringProvider
import edifikana_lib.Res
import edifikana_lib.title_timecard_staff_list
import kotlinx.coroutines.launch

/**
 * Represents the UI state of the Time Card Staff List screen.
 */
class TimeCardStaffListViewModel(
    private val staffManager: StaffManager,
    private val stringProvider: StringProvider,
    dependencies: ViewModelDependencies,
) : BaseViewModel<TimeCardStaffListEvent, TimeCardStaffListUIState>(
    dependencies,
    TimeCardStaffListUIState.Empty,
    TAG
) {

    /**
     * Load staff members.
     */
    fun loadStaffs() = viewModelScope.launch {
        updateUiState { it.copy(isLoading = true) }
        val result = staffManager.getStaffList()

        if (result.isFailure) {
            val state =
                TimeCardStaffListUIState(
                    emptyList(),
                    false,
                    stringProvider.getString(Res.string.title_timecard_staff_list),
                )
            updateUiState { state }
        } else {
            val staffs = result.getOrThrow()
            val state =
                TimeCardStaffListUIState(
                    staffs.map {
                        it.toUIModel()
                    },
                    false,
                    stringProvider.getString(Res.string.title_timecard_staff_list)
                )
            updateUiState { state }
        }
    }

    /**
     * Navigate to staff member.
     */
    fun navigateToStaff(staffPK: StaffId) = viewModelScope.launch {
        emitWindowEvent(
            EdifikanaWindowsEvent.NavigateToScreen(
                ManagementDestination.TimeCardSingleStaffDestination(staffPK)
            )
        )
    }

    /**
     * Navigate back.
     */
    fun navigateBack() = viewModelScope.launch {
        emitWindowEvent(
            EdifikanaWindowsEvent.NavigateBack
        )
    }

    companion object {
        private const val TAG = "TimeCardStaffListViewModel"
    }
}

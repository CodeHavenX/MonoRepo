package com.cramsan.edifikana.client.lib.features.root.main.timecard.stafflist

import com.cramsan.edifikana.client.lib.features.root.main.MainActivityEvent
import com.cramsan.edifikana.client.lib.features.root.main.MainRouteDestination
import com.cramsan.edifikana.client.lib.managers.StaffManager
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import edifikana_lib.Res
import edifikana_lib.title_timecard_staff_list
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

/**
 * Represents the UI state of the Staff List screen.
 */
class StaffListViewModel(
    private val staffManager: StaffManager,
    dependencies: ViewModelDependencies,
) : BaseViewModel(dependencies) {

    private val _uiState = MutableStateFlow(
        StaffListUIState(
            emptyList(),
            true,
            "",
        )
    )
    val uiState: StateFlow<StaffListUIState> = _uiState

    private val _event = MutableSharedFlow<StaffListEvent>()

    /**
     * Event flow.
     */
    val event: SharedFlow<StaffListEvent> = _event

    /**
     * Load staff members.
     */
    fun loadStaffs() = viewModelScope.launch {
        _uiState.value = _uiState.value.copy(isLoading = true)
        val result = staffManager.getStaffList()

        if (result.isFailure) {
            _uiState.value = StaffListUIState(
                emptyList(),
                false,
                getString(Res.string.title_timecard_staff_list),
            )
        } else {
            val staffs = result.getOrThrow()
            _uiState.value = StaffListUIState(
                staffs.map {
                    it.toUIModel()
                },
                false,
                getString(Res.string.title_timecard_staff_list)
            )
        }
    }

    /**
     * Navigate to staff member.
     */
    fun navigateToStaff(staffPK: StaffId) = viewModelScope.launch {
        _event.emit(
            StaffListEvent.TriggerMainActivityEvent(
                MainActivityEvent.Navigate(MainRouteDestination.TimeCardSingleStaffDestination(staffPK))
            )
        )
    }

    /**
     * Navigate to add staff member.
     */
    fun navigateToAddStaff() = viewModelScope.launch {
        _event.emit(
            StaffListEvent.TriggerMainActivityEvent(
                MainActivityEvent.Navigate(MainRouteDestination.TimeCardAddStaffDestination)
            )
        )
    }
}

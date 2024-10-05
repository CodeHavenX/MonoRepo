package com.cramsan.edifikana.client.lib.features.timecard.stafflist

import com.cramsan.edifikana.client.lib.features.base.EdifikanaBaseViewModel
import com.cramsan.edifikana.client.lib.features.main.MainActivityEvent
import com.cramsan.edifikana.client.lib.features.main.Route
import com.cramsan.edifikana.client.lib.managers.StaffManager
import com.cramsan.edifikana.lib.StaffPK
import com.cramsan.framework.core.DispatcherProvider
import edifikana_lib.Res
import edifikana_lib.title_timecard_staff_list
import kotlinx.coroutines.CoroutineExceptionHandler
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
    exceptionHandler: CoroutineExceptionHandler,
    dispatcherProvider: DispatcherProvider,
) : EdifikanaBaseViewModel(exceptionHandler, dispatcherProvider) {

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
        val result = staffManager.getStaffs()

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
    fun navigateToStaff(staffPK: StaffPK) = viewModelScope.launch {
        _event.emit(
            StaffListEvent.TriggerMainActivityEvent(
                MainActivityEvent.Navigate(Route.toTimeCardSingleStaffRoute(staffPK))
            )
        )
    }

    /**
     * Navigate to add staff member.
     */
    fun navigateToAddStaff() = viewModelScope.launch {
        _event.emit(
            StaffListEvent.TriggerMainActivityEvent(
                MainActivityEvent.Navigate(Route.toTimeCardAddStaffRoute())
            )
        )
    }
}

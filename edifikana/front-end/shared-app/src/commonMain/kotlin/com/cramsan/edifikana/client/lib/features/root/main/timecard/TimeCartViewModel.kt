package com.cramsan.edifikana.client.lib.features.root.main.timecard

import com.cramsan.edifikana.client.lib.features.root.main.MainActivityEvent
import com.cramsan.edifikana.client.lib.features.root.main.MainRouteDestination
import com.cramsan.edifikana.client.lib.managers.StaffManager
import com.cramsan.edifikana.client.lib.managers.TimeCardManager
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import edifikana_lib.Res
import edifikana_lib.title_timecard
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

/**
 * Represents the UI state of the Time Card screen.
 */
class TimeCartViewModel(
    private val timeCardManager: TimeCardManager,
    private val staffManager: StaffManager,
    dependencies: ViewModelDependencies,
) : BaseViewModel(dependencies) {

    private val _uiState = MutableStateFlow(
        TimeCardUIState(
            emptyList(),
            true,
            "",
        )
    )

    /**
     * UI state flow.
     */
    val uiState: StateFlow<TimeCardUIState> = _uiState

    private val _event = MutableSharedFlow<TimeCardEvent>()

    /**
     * Event flow.
     */
    val event: SharedFlow<TimeCardEvent> = _event

    /**
     * Load events.
     */
    fun loadEvents() = viewModelScope.launch {
        _uiState.value = _uiState.value.copy(isLoading = true)
        val staffsJob = async { staffManager.getStaffList() }
        val result = timeCardManager.getAllRecords()
        val staffsResult = staffsJob.await()
        if (result.isFailure || staffsResult.isFailure) {
            _uiState.value = TimeCardUIState(emptyList(), false, getString(Res.string.title_timecard))
        } else {
            val events = result.getOrThrow()
            val staffs = staffsResult.getOrThrow()
            _uiState.value = TimeCardUIState(
                events.toUIModel(staffs),
                false,
                getString(Res.string.title_timecard)
            )
        }
    }

    /**
     * Navigate to staff.
     */
    fun navigateToStaff(staffPK: StaffId) = viewModelScope.launch {
        _event.emit(
            TimeCardEvent.TriggerMainActivityEvent(
                MainActivityEvent.Navigate(MainRouteDestination.TimeCardSingleStaffDestination(staffPK))
            )
        )
    }

    /**
     * Navigate to staff list.
     */
    fun navigateToStaffList() = viewModelScope.launch {
        _event.emit(
            TimeCardEvent.TriggerMainActivityEvent(
                MainActivityEvent.Navigate(MainRouteDestination.TimeCardStaffListDestination)
            )
        )
    }
}

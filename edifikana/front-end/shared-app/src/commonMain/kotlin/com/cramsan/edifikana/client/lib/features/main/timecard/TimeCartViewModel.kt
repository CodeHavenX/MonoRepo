package com.cramsan.edifikana.client.lib.features.main.timecard

import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationEvent
import com.cramsan.edifikana.client.lib.features.main.MainRouteDestination
import com.cramsan.edifikana.client.lib.managers.StaffManager
import com.cramsan.edifikana.client.lib.managers.TimeCardManager
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import edifikana_lib.Res
import edifikana_lib.title_timecard
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

/**
 * Represents the UI state of the Time Card screen.
 */
class TimeCartViewModel(
    private val timeCardManager: TimeCardManager,
    private val staffManager: StaffManager,
    dependencies: ViewModelDependencies,
) : BaseViewModel<TimeCardEvent, TimeCardUIState>(dependencies, TimeCardUIState.Empty, TAG) {

    /**
     * Load events.
     */
    fun loadEvents() = viewModelScope.launch {
        updateUiState { it.copy(isLoading = true) }
        val staffsJob = async { staffManager.getStaffList() }
        val result = timeCardManager.getAllRecords()
        val staffsResult = staffsJob.await()
        if (result.isFailure || staffsResult.isFailure) {
            val updatedState = TimeCardUIState(emptyList(), false, getString(Res.string.title_timecard))
            updateUiState { updatedState }
        } else {
            val events = result.getOrThrow()
            val staffs = staffsResult.getOrThrow()
            val updatedState = TimeCardUIState(
                events.toUIModel(staffs),
                false,
                getString(Res.string.title_timecard)
            )
            updateUiState { updatedState }
        }
    }

    /**
     * Navigate to staff.
     */
    fun navigateToStaff(staffPK: StaffId) = viewModelScope.launch {
        emitEvent(
            TimeCardEvent.TriggerEdifikanaApplicationEvent(
                EdifikanaApplicationEvent.NavigateToScreem(MainRouteDestination.TimeCardSingleStaffDestination(staffPK))
            )
        )
    }

    /**
     * Navigate to staff list.
     */
    fun navigateToStaffList() = viewModelScope.launch {
        emitEvent(
            TimeCardEvent.TriggerEdifikanaApplicationEvent(
                EdifikanaApplicationEvent.NavigateToScreem(MainRouteDestination.TimeCardStaffListDestination)
            )
        )
    }

    /**
     * Navigate back.
     */
    fun navigateBack() {
        viewModelScope.launch {
            emitEvent(TimeCardEvent.TriggerEdifikanaApplicationEvent(EdifikanaApplicationEvent.NavigateBack()))
        }
    }

    companion object {
        private const val TAG = "TimeCartViewModel"
    }
}

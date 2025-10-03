package com.cramsan.edifikana.client.lib.features.management.timecard

import com.cramsan.edifikana.client.lib.features.management.ManagementDestination
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.EmployeeManager
import com.cramsan.edifikana.client.lib.managers.TimeCardManager
import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.core.compose.resources.StringProvider
import edifikana_lib.Res
import edifikana_lib.title_timecard
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/**
 * Represents the UI state of the Time Card screen.
 */
class TimeCartViewModel(
    private val timeCardManager: TimeCardManager,
    private val employeeManager: EmployeeManager,
    private val stringProvider: StringProvider,
    dependencies: ViewModelDependencies,
) : BaseViewModel<TimeCardEvent, TimeCardUIState>(
    dependencies,
    TimeCardUIState.Empty,
    TAG
) {

    /**
     * Load events.
     */
    fun loadEvents() = viewModelScope.launch {
        updateUiState { it.copy(isLoading = true) }
        val employeesJob = async { employeeManager.getEmployeeList() }
        val result = timeCardManager.getAllRecords()
        val employeesResult = employeesJob.await()
        if (result.isFailure || employeesResult.isFailure) {
            val updatedState =
                TimeCardUIState(
                    emptyList(),
                    false,
                    stringProvider.getString(Res.string.title_timecard)
                )
            updateUiState { updatedState }
        } else {
            val events = result.getOrThrow()
            val employees = employeesResult.getOrThrow()
            val updatedState =
                TimeCardUIState(
                    events.toUIModel(employees, stringProvider),
                    false,
                    stringProvider.getString(Res.string.title_timecard)
                )
            updateUiState { updatedState }
        }
    }

    /**
     * Navigate to employee.
     */
    fun navigateToEmployee(employeePK: EmployeeId) = viewModelScope.launch {
        emitWindowEvent(
            EdifikanaWindowsEvent.NavigateToScreen(ManagementDestination.TimeCardSingleEmployeeDestination(employeePK))
        )
    }

    /**
     * Navigate to employee list.
     */
    fun navigateToEmployeeList() = viewModelScope.launch {
        emitWindowEvent(
            EdifikanaWindowsEvent.NavigateToScreen(ManagementDestination.TimeCardEmployeeListDestination)
        )
    }

    /**
     * Navigate back.
     */
    fun navigateBack() {
        viewModelScope.launch {
            emitWindowEvent(EdifikanaWindowsEvent.NavigateBack)
        }
    }

    companion object {
        private const val TAG = "TimeCartViewModel"
    }
}

package com.cramsan.edifikana.client.lib.features.management.timecardemployeelist

import com.cramsan.edifikana.client.lib.features.management.ManagementDestination
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.EmployeeManager
import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.core.compose.resources.StringProvider
import edifikana_lib.Res
import edifikana_lib.title_timecard_employee_list
import kotlinx.coroutines.launch

/**
 * Represents the UI state of the Time Card Employee List screen.
 */
class TimeCardEmployeeListViewModel(
    private val employeeManager: EmployeeManager,
    private val stringProvider: StringProvider,
    dependencies: ViewModelDependencies,
) : BaseViewModel<TimeCardEmployeeListEvent, TimeCardEmployeeListUIState>(
    dependencies,
    TimeCardEmployeeListUIState.Empty,
    TAG
) {

    /**
     * Load employee members.
     */
    fun loadEmployees() = viewModelScope.launch {
        updateUiState { it.copy(isLoading = true) }
        val result = employeeManager.getEmployeeList()

        if (result.isFailure) {
            val state =
                TimeCardEmployeeListUIState(
                    emptyList(),
                    false,
                    stringProvider.getString(Res.string.title_timecard_employee_list),
                )
            updateUiState { state }
        } else {
            val employees = result.getOrThrow()
            val state =
                TimeCardEmployeeListUIState(
                    employees.map {
                        it.toUIModel()
                    },
                    false,
                    stringProvider.getString(Res.string.title_timecard_employee_list)
                )
            updateUiState { state }
        }
    }

    /**
     * Navigate to employee member.
     */
    fun navigateToEmployee(employeePK: EmployeeId) = viewModelScope.launch {
        emitWindowEvent(
            EdifikanaWindowsEvent.NavigateToScreen(
                ManagementDestination.TimeCardSingleEmployeeDestination(employeePK)
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
        private const val TAG = "TimeCardEmployeeListViewModel"
    }
}

package com.cramsan.edifikana.client.lib.features.timecard.employeelist

import com.cramsan.edifikana.client.lib.features.base.EdifikanaBaseViewModel
import com.cramsan.edifikana.client.lib.features.main.MainActivityEvent
import com.cramsan.edifikana.client.lib.features.main.Route
import com.cramsan.edifikana.client.lib.managers.EmployeeManager
import com.cramsan.edifikana.lib.firestore.EmployeePK
import com.cramsan.framework.core.DispatcherProvider
import edifikana_lib.Res
import edifikana_lib.title_timecard_employee_list
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

class EmployeeListViewModel(
    private val employeeManager: EmployeeManager,
    exceptionHandler: CoroutineExceptionHandler,
    dispatcherProvider: DispatcherProvider,
) : EdifikanaBaseViewModel(exceptionHandler, dispatcherProvider) {

    private val _uiState = MutableStateFlow(
        EmployeeListUIState(
            emptyList(),
            true,
            "",
        )
    )
    val uiState: StateFlow<EmployeeListUIState> = _uiState

    private val _event = MutableSharedFlow<EmployeeListEvent>()
    val event: SharedFlow<EmployeeListEvent> = _event

    fun loadEmployees() = viewModelScope.launch {
        _uiState.value = _uiState.value.copy(isLoading = true)
        val result = employeeManager.getEmployees()

        if (result.isFailure) {
            _uiState.value = EmployeeListUIState(
                emptyList(),
                false,
                getString(Res.string.title_timecard_employee_list),
            )
        } else {
            val employees = result.getOrThrow()
            _uiState.value = EmployeeListUIState(
                employees.map {
                    it.toUIModel()
                },
                false,
                getString(Res.string.title_timecard_employee_list)
            )
        }
    }

    fun navigateToEmployee(employeePK: EmployeePK) = viewModelScope.launch {
        _event.emit(
            EmployeeListEvent.TriggerMainActivityEvent(
                MainActivityEvent.Navigate(Route.toTimeCardSingleEmployeeRoute(employeePK))
            )
        )
    }

    fun navigateToAddEmployee() = viewModelScope.launch {
        _event.emit(
            EmployeeListEvent.TriggerMainActivityEvent(
                MainActivityEvent.Navigate(Route.toTimeCardAddEmployeeRoute())
            )
        )
    }
}

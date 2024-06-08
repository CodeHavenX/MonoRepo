package com.cramsan.edifikana.client.android.features.timecard.employeelist

import android.content.Context
import com.cramsan.edifikana.client.android.R
import com.cramsan.edifikana.client.android.features.base.EdifikanaBaseViewModel
import com.cramsan.edifikana.client.lib.features.main.MainActivityEvent
import com.cramsan.edifikana.client.lib.features.main.Route
import com.cramsan.edifikana.client.lib.managers.EmployeeManager
import com.cramsan.edifikana.lib.firestore.EmployeePK
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmployeeListViewModel @Inject constructor(
    private val employeeManager: EmployeeManager,
    @ApplicationContext
    private val applicationContext: Context,
    exceptionHandler: CoroutineExceptionHandler,
) : EdifikanaBaseViewModel(exceptionHandler) {

    private val _uiState = MutableStateFlow(
        EmployeeListUIState(
            emptyList(),
            true,
            applicationContext.getString(R.string.title_timecard_employee_list)
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
                applicationContext.getString(R.string.title_timecard_employee_list),
            )
        } else {
            val employees = result.getOrThrow()
            _uiState.value = EmployeeListUIState(
                employees.map {
                    it.toUIModel()
                },
                false,
                applicationContext.getString(R.string.title_timecard_employee_list)
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

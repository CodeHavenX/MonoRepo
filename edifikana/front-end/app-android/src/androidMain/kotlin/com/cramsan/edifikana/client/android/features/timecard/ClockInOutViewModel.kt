package com.cramsan.edifikana.client.android.features.timecard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cramsan.edifikana.client.android.features.main.MainActivityEvent
import com.cramsan.edifikana.client.android.features.main.Route
import com.cramsan.edifikana.client.android.managers.EmployeeManager
import com.cramsan.edifikana.lib.firestore.EmployeePK
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

@HiltViewModel
class ClockInOutViewModel @Inject constructor(
    private val employeeManager: EmployeeManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TimeCardUIState(emptyList(), true))
    val uiState: StateFlow<TimeCardUIState> = _uiState

    private val _event = MutableSharedFlow<TimeCardEvent>()
    val event: SharedFlow<TimeCardEvent> = _event

    fun loadEmployees() = viewModelScope.launch {
        _uiState.value = _uiState.value.copy(isLoading = true)
        employeeManager.getEmployees().onSuccess { employees ->
            _uiState.value = TimeCardUIState(
                employees.map {
                    it.toUIModel()
                },
                false
            )
        }.onFailure {
            _uiState.value = TimeCardUIState(emptyList(), false)
        }
    }

    fun navigateToEmployee(employeePK: EmployeePK) = viewModelScope.launch {
        _event.emit(TimeCardEvent.TriggerMainActivityEvent(
            MainActivityEvent.Navigate("clockin/${employeePK.documentPath}")
        ))
    }

    fun navigateToAddEmployee() = viewModelScope.launch {
        _event.emit(TimeCardEvent.TriggerMainActivityEvent(
            MainActivityEvent.Navigate(Route.ClockInOutAddEmployee.route)
        ))
    }
}

package com.cramsan.edifikana.client.lib.features.timecard

import com.cramsan.edifikana.client.lib.features.base.EdifikanaBaseViewModel
import com.cramsan.edifikana.client.lib.features.main.MainActivityEvent
import com.cramsan.edifikana.client.lib.features.main.Route
import com.cramsan.edifikana.client.lib.managers.EmployeeManager
import com.cramsan.edifikana.client.lib.managers.TimeCardManager
import com.cramsan.edifikana.lib.firestore.EmployeePK
import com.cramsan.framework.core.DispatcherProvider
import edifikana_lib.Res
import edifikana_lib.title_timecard
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

class TimeCartViewModel(
    private val timeCardManager: TimeCardManager,
    private val employeeManager: EmployeeManager,
    exceptionHandler: CoroutineExceptionHandler,
    dispatcherProvider: DispatcherProvider,
) : EdifikanaBaseViewModel(exceptionHandler, dispatcherProvider) {

    private val _uiState = MutableStateFlow(
        TimeCardUIState(
            emptyList(),
            true,
            "",
        )
    )
    val uiState: StateFlow<TimeCardUIState> = _uiState

    private val _event = MutableSharedFlow<TimeCardEvent>()
    val event: SharedFlow<TimeCardEvent> = _event

    fun loadEvents() = viewModelScope.launch {
        _uiState.value = _uiState.value.copy(isLoading = true)
        val employeesJob = async { employeeManager.getEmployees() }
        val result = timeCardManager.getAllRecords()
        val employeesResult = employeesJob.await()
        if (result.isFailure || employeesResult.isFailure) {
            _uiState.value = TimeCardUIState(emptyList(), false, getString(Res.string.title_timecard))
        } else {
            val events = result.getOrThrow()
            val employees = employeesResult.getOrThrow()
            _uiState.value = TimeCardUIState(
                events.toUIModel(employees),
                false,
                getString(Res.string.title_timecard)
            )
        }
    }

    fun navigateToEmployee(employeePK: EmployeePK) = viewModelScope.launch {
        _event.emit(
            TimeCardEvent.TriggerMainActivityEvent(
                MainActivityEvent.Navigate(Route.toTimeCardSingleEmployeeRoute(employeePK))
            )
        )
    }

    fun navigateToEmployeeList() = viewModelScope.launch {
        _event.emit(
            TimeCardEvent.TriggerMainActivityEvent(
                MainActivityEvent.Navigate(Route.toTimeCardEmployeeListRoute())
            )
        )
    }
}

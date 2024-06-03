package com.cramsan.edifikana.client.android.features.timecard

import android.content.Context
import com.cramsan.edifikana.client.android.R
import com.cramsan.edifikana.client.android.features.base.EdifikanaBaseViewModel
import com.cramsan.edifikana.client.android.features.main.MainActivityEvent
import com.cramsan.edifikana.client.android.features.main.Route
import com.cramsan.edifikana.client.android.managers.EmployeeManager
import com.cramsan.edifikana.client.android.managers.TimeCardManager
import com.cramsan.edifikana.lib.firestore.EmployeePK
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimeCartViewModel @Inject constructor(
    private val timeCardManager: TimeCardManager,
    private val employeeManager: EmployeeManager,
    @ApplicationContext
    private val context: Context,
    exceptionHandler: CoroutineExceptionHandler,
) : EdifikanaBaseViewModel(exceptionHandler) {

    private val _uiState = MutableStateFlow(
        TimeCardUIState(
            emptyList(),
            true,
            context.getString(R.string.title_timecard),
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
            _uiState.value = TimeCardUIState(emptyList(), false, context.getString(R.string.title_timecard))
        } else {
            val events = result.getOrThrow()
            val employees = employeesResult.getOrThrow()
            _uiState.value = TimeCardUIState(
                events.toUIModel(employees),
                false,
                context.getString(R.string.title_timecard)
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

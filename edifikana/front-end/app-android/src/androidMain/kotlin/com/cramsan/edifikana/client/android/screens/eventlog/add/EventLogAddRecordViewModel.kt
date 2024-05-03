package com.cramsan.edifikana.client.android.screens.eventlog.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cramsan.edifikana.client.android.R
import com.cramsan.edifikana.client.android.managers.EmployeeManager
import com.cramsan.edifikana.client.android.managers.EventLogManager
import com.cramsan.edifikana.client.android.screens.eventlog.add.EventLogAddRecordUIState.Loading.toUIModel
import com.cramsan.edifikana.lib.firestore.EmployeePK
import com.cramsan.edifikana.lib.firestore.EventLogRecord
import com.cramsan.edifikana.lib.firestore.EventType
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

@HiltViewModel
class EventLogAddRecordViewModel @Inject constructor(
    private val employeeManager: EmployeeManager,
    private val eventLogManager: EventLogManager,
    val clock: Clock,
): ViewModel() {

    private val _uiState = MutableStateFlow<EventLogAddRecordUIState>(EventLogAddRecordUIState.Loading)
    val uiState: StateFlow<EventLogAddRecordUIState> = _uiState

    private val _event = MutableStateFlow<EventLogAddRecordUIEvent>(EventLogAddRecordUIEvent.Noop)
    val event: StateFlow<EventLogAddRecordUIEvent> = _event

    fun loadEmployees() = viewModelScope.launch {
        _uiState.value = EventLogAddRecordUIState.Loading
        employeeManager.getEmployees().onSuccess { employees ->
            val employeeList = employees.map {
                it.toUIModel()
            }
            _uiState.value = EventLogAddRecordUIState.Success(
                employeeList + EventLogAddRecordUIState.EmployeeUIModel("Otro", null)
            )
        }.onFailure {
            _uiState.value = EventLogAddRecordUIState.Error(R.string.app_name)
        }
    }

    fun addRecord(
        employeeDocumentId: EmployeePK?,
        unit: String?,
        eventType: EventType?,
        fallbackEmployeeName: String?,
        fallbackEventType: String?,
        summary: String?,
        description: String?,
    ) = viewModelScope.launch {
        _uiState.value = EventLogAddRecordUIState.Loading

        val eventLogRecord = EventLogRecord(
            employeeDocumentId = employeeDocumentId?.documentPath,
            timeRecorded = clock.now().epochSeconds,
            unit = unit,
            eventType = eventType ?: EventType.INCIDENT,
            fallbackEmployeeName = fallbackEmployeeName,
            fallbackEventType = fallbackEventType,
            summary = summary,
            description = description,
        )
        val result = eventLogManager.addRecord(eventLogRecord)

        result.onSuccess {
            _event.value = EventLogAddRecordUIEvent.OnAddCompleted
        }.onFailure {
            _uiState.value = EventLogAddRecordUIState.Error(R.string.app_name)
        }
    }
}
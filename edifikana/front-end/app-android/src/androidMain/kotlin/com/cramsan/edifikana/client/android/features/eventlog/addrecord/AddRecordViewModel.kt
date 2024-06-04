package com.cramsan.edifikana.client.android.features.eventlog.addrecord

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.cramsan.edifikana.client.android.R
import com.cramsan.edifikana.client.android.features.base.EdifikanaBaseViewModel
import com.cramsan.edifikana.client.android.features.main.MainActivityEvent
import com.cramsan.edifikana.client.android.managers.EmployeeManager
import com.cramsan.edifikana.client.android.managers.EventLogManager
import com.cramsan.edifikana.client.android.models.EventLogRecordModel
import com.cramsan.edifikana.lib.firestore.EmployeePK
import com.cramsan.edifikana.lib.firestore.EventLogRecordPK
import com.cramsan.edifikana.lib.firestore.EventType
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import javax.inject.Inject

@HiltViewModel
class AddRecordViewModel @Inject constructor(
    private val employeeManager: EmployeeManager,
    private val eventLogManager: EventLogManager,
    private val clock: Clock,
    @ApplicationContext
    private val context: Context,
    exceptionHandler: CoroutineExceptionHandler,
) : EdifikanaBaseViewModel(exceptionHandler) {

    private val _uiState = MutableStateFlow(
        AddRecordUIState(emptyList(), true, context.getString(R.string.title_event_log_add))
    )
    val uiState: StateFlow<AddRecordUIState> = _uiState

    private val _event = MutableSharedFlow<AddRecordEvent>()
    val event: SharedFlow<AddRecordEvent> = _event

    fun loadEmployees() = viewModelScope.launch {
        _uiState.value = _uiState.value.copy(isLoading = true)
        val result = employeeManager.getEmployees()

        if (result.isFailure) {
            _uiState.value = AddRecordUIState(listOf(), false, context.getString(R.string.title_event_log_add))
        } else {
            val employees = result.getOrThrow()
            val employeeList = employees.map {
                it.toUIModel()
            }
            _uiState.value = AddRecordUIState(
                // TODO: Move this resources
                employeeList + AddRecordUIModel(context.getString(R.string.string_other), null),
                false,
                context.getString(R.string.title_event_log_add)
            )
        }
    }

    @Suppress("ComplexCondition")
    fun addRecord(
        employeeDocumentId: EmployeePK?,
        unit: String?,
        eventType: EventType?,
        fallbackEmployeeName: String?,
        fallbackEventType: String?,
        summary: String?,
        description: String?,
    ) = viewModelScope.launch {
        _uiState.value = _uiState.value.copy(isLoading = true)

        if (
            (employeeDocumentId == null && fallbackEmployeeName.isNullOrBlank()) ||
            (eventType == null && fallbackEventType.isNullOrBlank()) ||
            unit.isNullOrBlank() ||
            summary.isNullOrBlank() ||
            description.isNullOrBlank()
        ) {
            _uiState.value = _uiState.value.copy(isLoading = false)
            return@launch
        }

        val eventLogRecord = EventLogRecordModel(
            id = EventLogRecordPK(""),
            employeePk = employeeDocumentId,
            timeRecorded = clock.now().epochSeconds,
            unit = unit.trim(),
            eventType = eventType ?: EventType.INCIDENT,
            fallbackEmployeeName = fallbackEmployeeName?.trim(),
            fallbackEventType = fallbackEventType?.trim(),
            summary = summary.trim(),
            description = description.trim(),
            attachments = emptyList(),
        )
        val result = eventLogManager.addRecord(eventLogRecord)

        if (result.isFailure) {
            _uiState.value = _uiState.value.copy(employees = emptyList(), isLoading = false)
        } else {
            _event.emit(
                AddRecordEvent.TriggerMainActivityEvent(
                    MainActivityEvent.NavigateBack()
                )
            )
        }
    }
}

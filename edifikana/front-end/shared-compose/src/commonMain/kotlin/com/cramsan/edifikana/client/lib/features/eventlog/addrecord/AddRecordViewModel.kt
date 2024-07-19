package com.cramsan.edifikana.client.lib.features.eventlog.addrecord

import com.cramsan.edifikana.client.lib.features.base.EdifikanaBaseViewModel
import com.cramsan.edifikana.client.lib.features.main.MainActivityEvent
import com.cramsan.edifikana.client.lib.managers.EmployeeManager
import com.cramsan.edifikana.client.lib.managers.EventLogManager
import com.cramsan.edifikana.client.lib.models.EventLogRecordModel
import com.cramsan.edifikana.lib.EmployeePK
import com.cramsan.edifikana.lib.firestore.EventType
import com.cramsan.framework.core.DispatcherProvider
import edifikana_lib.Res
import edifikana_lib.string_other
import edifikana_lib.title_event_log_add
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.jetbrains.compose.resources.getString

class AddRecordViewModel(
    private val employeeManager: EmployeeManager,
    private val eventLogManager: EventLogManager,
    private val clock: Clock,
    exceptionHandler: CoroutineExceptionHandler,
    dispatcherProvider: DispatcherProvider,
) : EdifikanaBaseViewModel(exceptionHandler, dispatcherProvider) {

    private val _uiState = MutableStateFlow(
        AddRecordUIState(emptyList(), true, "")
    )
    val uiState: StateFlow<AddRecordUIState> = _uiState

    private val _event = MutableSharedFlow<AddRecordEvent>()
    val event: SharedFlow<AddRecordEvent> = _event

    fun loadEmployees() = viewModelScope.launch {
        _uiState.value = _uiState.value.copy(isLoading = true)
        val result = employeeManager.getEmployees()

        if (result.isFailure) {
            _uiState.value = AddRecordUIState(listOf(), false, getString(Res.string.title_event_log_add))
        } else {
            val employees = result.getOrThrow()
            val employeeList = employees.map {
                it.toUIModel()
            }
            _uiState.value = AddRecordUIState(
                // TODO: Move this resources
                employeeList + AddRecordUIModel(getString(Res.string.string_other), null),
                false,
                getString(Res.string.title_event_log_add)
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

        val eventLogRecord = EventLogRecordModel.createTemporary(
            employeePk = employeeDocumentId,
            timeRecorded = clock.now().epochSeconds,
            unit = unit.trim(),
            eventType = eventType ?: EventType.INCIDENT,
            fallbackEmployeeName = fallbackEmployeeName?.trim(),
            fallbackEventType = fallbackEventType?.trim(),
            summary = summary.trim(),
            description = description.trim(),
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

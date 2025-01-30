package com.cramsan.edifikana.client.lib.features.root.main.eventlog.addrecord

import com.cramsan.edifikana.client.lib.features.root.main.MainActivityEvent
import com.cramsan.edifikana.client.lib.managers.EventLogManager
import com.cramsan.edifikana.client.lib.managers.PropertyManager
import com.cramsan.edifikana.client.lib.managers.StaffManager
import com.cramsan.edifikana.client.lib.models.EventLogRecordModel
import com.cramsan.edifikana.lib.model.EventLogEventType
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import edifikana_lib.Res
import edifikana_lib.string_other
import edifikana_lib.title_event_log_add
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.jetbrains.compose.resources.getString

/**
 * Represents the UI state of the Add Record screen.
 */
class AddRecordViewModel(
    private val staffManager: StaffManager,
    private val eventLogManager: EventLogManager,
    private val clock: Clock,
    private val propertyManager: PropertyManager,
    dependencies: ViewModelDependencies,
) : BaseViewModel(dependencies) {

    private val _uiState = MutableStateFlow(
        AddRecordUIState(emptyList(), true, "")
    )

    /**
     * UI state flow.
     */
    val uiState: StateFlow<AddRecordUIState> = _uiState

    private val _event = MutableSharedFlow<AddRecordEvent>()

    /**
     * Event flow.
     */
    val event: SharedFlow<AddRecordEvent> = _event

    /**
     * Load staff members.
     */
    fun loadStaffs() = viewModelScope.launch {
        _uiState.value = _uiState.value.copy(isLoading = true)
        val result = staffManager.getStaffList()

        if (result.isFailure) {
            _uiState.value = AddRecordUIState(listOf(), false, getString(Res.string.title_event_log_add))
        } else {
            val staffs = result.getOrThrow()
            val staffList = staffs.map {
                it.toUIModel()
            }
            _uiState.value = AddRecordUIState(
                // TODO: Move this resources
                staffList + AddRecordUIModel(getString(Res.string.string_other), null),
                false,
                getString(Res.string.title_event_log_add)
            )
        }
    }

    /**
     * Add a new record to the event log.
     */
    @Suppress("ComplexCondition")
    fun addRecord(
        staffDocumentId: StaffId?,
        unit: String?,
        eventType: EventLogEventType?,
        fallbackStaffName: String?,
        fallbackEventType: String?,
        title: String?,
        description: String?,
    ) = viewModelScope.launch {
        _uiState.value = _uiState.value.copy(isLoading = true)

        if (
            (staffDocumentId == null && fallbackStaffName.isNullOrBlank()) ||
            (eventType == null && fallbackEventType.isNullOrBlank()) ||
            unit.isNullOrBlank() ||
            title.isNullOrBlank() ||
            description.isNullOrBlank()
        ) {
            _uiState.value = _uiState.value.copy(isLoading = false)
            return@launch
        }

        val property: PropertyId = propertyManager.activeProperty().value ?: error("No active property")
        val eventLogRecord = EventLogRecordModel.createTemporary(
            staffPk = staffDocumentId,
            timeRecorded = clock.now().epochSeconds,
            unit = unit.trim(),
            eventType = eventType ?: EventLogEventType.INCIDENT,
            fallbackStaffName = fallbackStaffName?.trim(),
            fallbackEventType = fallbackEventType?.trim(),
            title = title.trim(),
            description = description.trim(),
            propertyId = property,
        )
        val result = eventLogManager.addRecord(eventLogRecord)

        if (result.isFailure) {
            _uiState.value = _uiState.value.copy(records = emptyList(), isLoading = false)
        } else {
            _event.emit(
                AddRecordEvent.TriggerMainActivityEvent(
                    MainActivityEvent.NavigateBack()
                )
            )
        }
    }
}

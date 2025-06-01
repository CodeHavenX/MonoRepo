package com.cramsan.edifikana.client.lib.features.management.addrecord

import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.EventLogManager
import com.cramsan.edifikana.client.lib.managers.PropertyManager
import com.cramsan.edifikana.client.lib.managers.StaffManager
import com.cramsan.edifikana.client.lib.models.EventLogRecordModel
import com.cramsan.edifikana.lib.model.EventLogEventType
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.core.compose.resources.StringProvider
import com.cramsan.framework.utils.time.Chronos
import edifikana_lib.Res
import edifikana_lib.string_other
import edifikana_lib.title_event_log_add
import kotlinx.coroutines.launch

/**
 * Represents the UI state of the Add Record screen.
 */
class AddRecordViewModel(
    private val staffManager: StaffManager,
    private val eventLogManager: EventLogManager,
    private val propertyManager: PropertyManager,
    private val stringProvider: StringProvider,
    dependencies: ViewModelDependencies,
) : BaseViewModel<AddRecordEvent, AddRecordUIState>(
    dependencies,
    AddRecordUIState.Empty,
    TAG
) {

    /**
     * Load staff members.
     */
    fun loadStaffs() = viewModelScope.launch {
        updateUiState { it.copy(isLoading = true) }
        val result = staffManager.getStaffList()

        if (result.isFailure) {
            val state =
                AddRecordUIState(
                    listOf(),
                    false,
                    stringProvider.getString(Res.string.title_event_log_add)
                )
            updateUiState { state }
        } else {
            val staffs = result.getOrThrow()
            val staffList = staffs.map {
                it.toUIModel()
            }
            val state =
                AddRecordUIState(
                    // TODO: Move this resources
                    staffList + AddRecordUIModel(
                        stringProvider.getString(Res.string.string_other),
                        null
                    ),
                    false,
                    stringProvider.getString(Res.string.title_event_log_add)
                )
            updateUiState { state }
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
        updateUiState { it.copy(isLoading = true) }

        if (
            (staffDocumentId == null && fallbackStaffName.isNullOrBlank()) ||
            (eventType == null && fallbackEventType.isNullOrBlank()) ||
            unit.isNullOrBlank() ||
            title.isNullOrBlank() ||
            description.isNullOrBlank()
        ) {
            updateUiState { it.copy(isLoading = false) }
            return@launch
        }

        val property: PropertyId = propertyManager.activeProperty().value ?: error("No active property")
        val eventLogRecord = EventLogRecordModel.createTemporary(
            staffPk = staffDocumentId,
            timeRecorded = Chronos.currentInstant().epochSeconds,
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
            updateUiState { it.copy(isLoading = false) }
        } else {
            emitWindowEvent(
                EdifikanaWindowsEvent.NavigateBack
            )
        }
    }

    /**
     * Navigate back.
     */
    fun navigateBack() {
        viewModelScope.launch {
            emitWindowEvent(EdifikanaWindowsEvent.NavigateBack)
        }
    }

    companion object {
        private const val TAG = "AddRecordViewModel"
    }
}

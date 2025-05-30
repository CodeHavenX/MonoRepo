package com.cramsan.edifikana.client.lib.features.main.eventlog

import com.cramsan.edifikana.client.lib.features.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.features.management.ManagementDestination
import com.cramsan.edifikana.client.lib.managers.EventLogManager
import com.cramsan.edifikana.lib.model.EventLogEntryId
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.core.compose.resources.StringProvider
import com.cramsan.framework.logging.logW
import edifikana_lib.Res
import edifikana_lib.error_message_currently_uploading
import edifikana_lib.title_event_log
import kotlinx.coroutines.launch

/**
 * Represents the UI state of the Event Log screen.
 */
class EventLogViewModel(
    private val eventLogManager: EventLogManager,
    private val stringProvider: StringProvider,
    dependencies: ViewModelDependencies,
) : BaseViewModel<EventLogEvent, EventLogUIState>(dependencies, EventLogUIState.Empty, TAG) {

    /**
     * Load records.
     */
    fun loadRecords() = viewModelScope.launch {
        updateUiState { it.copy(isLoading = true) }
        val result = eventLogManager.getRecords()

        if (result.isFailure) {
            val title = stringProvider.getString(Res.string.title_event_log)
            updateUiState {
                EventLogUIState(emptyList(), false, title)
            }
        } else {
            val records = result.getOrThrow()
            val recordList = EventLogUIState(
                records.map { it.toUIModel(stringProvider) },
                false,
                stringProvider.getString(Res.string.title_event_log)
            )
            updateUiState { recordList }
        }
    }

    /**
     * Open a record screen.
     */
    fun openRecordScreen(recordPk: EventLogEntryId?) = viewModelScope.launch {
        if (recordPk == null) {
            logW(TAG, "Record PK is null")
            emitWindowEvent(
                EdifikanaWindowsEvent.ShowSnackbar(
                    stringProvider.getString(Res.string.error_message_currently_uploading)
                )
            )
        } else {
            emitWindowEvent(
                EdifikanaWindowsEvent.NavigateToScreen(
                    ManagementDestination.EventLogSingleItemDestination(recordPk)
                )
            )
        }
    }

    /**
     * Open the add record screen.
     */
    fun openAddRecordScreen() = viewModelScope.launch {
        emitWindowEvent(
            EdifikanaWindowsEvent.NavigateToScreen(ManagementDestination.EventLogAddItemDestination)
        )
    }

    /**
     * Navigate back.
     */
    fun navigateBack() {
        viewModelScope.launch {
            emitWindowEvent(
                EdifikanaWindowsEvent.NavigateBack
            )
        }
    }

    companion object {
        private const val TAG = "EventLogViewModel"
    }
}

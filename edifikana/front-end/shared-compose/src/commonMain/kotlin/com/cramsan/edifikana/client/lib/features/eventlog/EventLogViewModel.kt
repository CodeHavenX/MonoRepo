package com.cramsan.edifikana.client.lib.features.eventlog

import com.cramsan.edifikana.client.lib.features.base.EdifikanaBaseViewModel
import com.cramsan.edifikana.client.lib.features.main.MainActivityEvent
import com.cramsan.edifikana.client.lib.features.main.Route
import com.cramsan.edifikana.client.lib.managers.EventLogManager
import com.cramsan.edifikana.lib.EventLogRecordPK
import com.cramsan.framework.core.DispatcherProvider
import com.cramsan.framework.logging.logW
import edifikana_lib.Res
import edifikana_lib.error_message_currently_uploading
import edifikana_lib.title_event_log
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

class EventLogViewModel(
    private val eventLogManager: EventLogManager,
    dispatcherProvider: DispatcherProvider,
    exceptionHandler: CoroutineExceptionHandler,
) : EdifikanaBaseViewModel(exceptionHandler, dispatcherProvider) {

    private val _uiState = MutableStateFlow(
        EventLogUIState(emptyList(), true, "")
    )
    val uiState: StateFlow<EventLogUIState> = _uiState

    private val _event = MutableSharedFlow<EventLogEvent>()
    val event: SharedFlow<EventLogEvent> = _event

    fun loadRecords() = viewModelScope.launch {
        _uiState.value = _uiState.value.copy(isLoading = true)
        val result = eventLogManager.getRecords()

        if (result.isFailure) {
            _uiState.value = EventLogUIState(emptyList(), false, getString(Res.string.title_event_log))
        } else {
            val records = result.getOrThrow()
            val recordList = EventLogUIState(
                records.map { it.toUIModel() },
                false,
                getString(Res.string.title_event_log)
            )
            _uiState.value = recordList
        }
    }

    fun openRecordScreen(recordPk: EventLogRecordPK?) = viewModelScope.launch {
        if (recordPk == null) {
            logW(TAG, "Record PK is null")
            _event.emit(
                EventLogEvent.TriggerMainActivityEvent(
                    MainActivityEvent.ShowSnackbar(getString(Res.string.error_message_currently_uploading))
                )
            )
        } else {
            _event.emit(
                EventLogEvent.TriggerMainActivityEvent(
                    MainActivityEvent.Navigate(Route.toEventLogSingleItemRoute(recordPk))
                )
            )
        }
    }

    fun openAddRecordScreen() = viewModelScope.launch {
        _event.emit(
            EventLogEvent.TriggerMainActivityEvent(
                MainActivityEvent.Navigate(Route.toEventLogAddItemRoute())
            )
        )
    }

    companion object {
        private const val TAG = "EventLogViewModel"
    }
}

package com.cramsan.edifikana.client.android.features.eventlog

import android.content.Context
import com.cramsan.edifikana.client.android.R
import com.cramsan.edifikana.client.android.features.base.EdifikanaBaseViewModel
import com.cramsan.edifikana.client.android.features.main.MainActivityEvent
import com.cramsan.edifikana.client.android.features.main.Route
import com.cramsan.edifikana.client.android.managers.EventLogManager
import com.cramsan.edifikana.lib.firestore.EventLogRecordPK
import com.cramsan.framework.logging.logW
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventLogViewModel @Inject constructor(
    private val eventLogManager: EventLogManager,
    @ApplicationContext
    private val context: Context,
    exceptionHandler: CoroutineExceptionHandler,
) : EdifikanaBaseViewModel(exceptionHandler) {

    private val _uiState = MutableStateFlow(
        EventLogUIState(emptyList(), true, context.getString(R.string.title_event_log))
    )
    val uiState: StateFlow<EventLogUIState> = _uiState

    private val _event = MutableSharedFlow<EventLogEvent>()
    val event: SharedFlow<EventLogEvent> = _event

    fun loadRecords() = viewModelScope.launch {
        _uiState.value = _uiState.value.copy(isLoading = true)
        val result = eventLogManager.getRecords()

        if (result.isFailure) {
            _uiState.value = EventLogUIState(emptyList(), false, context.getString(R.string.title_event_log))
        } else {
            val records = result.getOrThrow()
            val recordList = EventLogUIState(
                records.map { it.toUIModel() },
                false,
                context.getString(R.string.title_event_log)
            )
            _uiState.value = recordList
        }
    }

    fun openRecordScreen(recordPk: EventLogRecordPK?) = viewModelScope.launch {
        if (recordPk == null) {
            logW(TAG, "Record PK is null")
            _event.emit(
                EventLogEvent.TriggerMainActivityEvent(
                    MainActivityEvent.ShowSnackbar(context.getString(R.string.error_message_currently_uploading))
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

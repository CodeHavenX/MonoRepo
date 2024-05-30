package com.cramsan.edifikana.client.android.features.eventlog

import com.cramsan.edifikana.client.android.features.base.EdifikanaBaseViewModel
import com.cramsan.edifikana.client.android.features.main.MainActivityEvent
import com.cramsan.edifikana.client.android.features.main.Route
import com.cramsan.edifikana.client.android.managers.EventLogManager
import com.cramsan.edifikana.lib.firestore.EventLogRecordPK
import dagger.hilt.android.lifecycle.HiltViewModel
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
    exceptionHandler: CoroutineExceptionHandler,
) : EdifikanaBaseViewModel(exceptionHandler) {

    private val _uiState = MutableStateFlow(EventLogUIState(emptyList(), true))
    val uiState: StateFlow<EventLogUIState> = _uiState

    private val _event = MutableSharedFlow<EventLogEvent>()
    val event: SharedFlow<EventLogEvent> = _event

    fun loadRecords() = viewModelScope.launch {
        _uiState.value = _uiState.value.copy(isLoading = true)
        val result = eventLogManager.getRecords()

        if (result.isFailure) {
            _uiState.value = EventLogUIState(emptyList(), false)
        } else {
            val records = result.getOrThrow()
            _uiState.value = EventLogUIState(records.map { it.toUIModel() }, false)
        }
    }

    fun openRecordScreen(recordPk: EventLogRecordPK) = viewModelScope.launch {
        _event.emit(
            EventLogEvent.TriggerMainActivityEvent(
                MainActivityEvent.Navigate("eventlog/${recordPk.documentPath}")
            )
        )
    }

    fun openAddRecordScreen() = viewModelScope.launch {
        _event.emit(
            EventLogEvent.TriggerMainActivityEvent(
                MainActivityEvent.Navigate(Route.EventLogAddItem.route)
            )
        )
    }
}

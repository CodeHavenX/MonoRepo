package com.cramsan.edifikana.client.android.features.eventlog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cramsan.edifikana.client.android.features.main.MainActivityEvent
import com.cramsan.edifikana.client.android.features.main.Route
import com.cramsan.edifikana.client.android.managers.EventLogManager
import com.cramsan.edifikana.client.android.models.EventLogRecordModel
import com.cramsan.edifikana.lib.firestore.EventLogRecordPK
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

@HiltViewModel
class EventLogViewModel @Inject constructor(
    private val eventLogManager: EventLogManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(EventLogUIState(emptyList(), true))
    val uiState: StateFlow<EventLogUIState> = _uiState

    private val _event = MutableSharedFlow<EventLogEvent>()
    val event: SharedFlow<EventLogEvent> = _event


    fun loadRecords() = viewModelScope.launch {
        _uiState.value = _uiState.value.copy(isLoading = true)
        eventLogManager.getRecords().onSuccess { records ->
            _uiState.value = EventLogUIState(records.map { it.toUIModel() }, false)
        }.onFailure { throwable ->
            throwable.printStackTrace()
            _uiState.value = EventLogUIState(emptyList(), false)
        }
    }

    fun openRecordScreen(recordPk: EventLogRecordPK) = viewModelScope.launch {
        _event.emit(EventLogEvent.TriggerMainActivityEvent(
            MainActivityEvent.Navigate("eventlog/${recordPk.documentPath}")
        ))
    }

    fun openAddRecordScreen() = viewModelScope.launch {
        _event.emit(EventLogEvent.TriggerMainActivityEvent(
            MainActivityEvent.Navigate(Route.EventLogAddItem.route)
        ))
    }
}

package com.cramsan.edifikana.client.android.screens.eventlog.single

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cramsan.edifikana.client.android.R
import com.cramsan.edifikana.client.android.managers.EventLogManager
import com.cramsan.edifikana.lib.firestore.EventLogRecordPK
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

@HiltViewModel
class EventLogSingleRecordViewModel @Inject constructor(
    private val eventLogManager: EventLogManager,
    val clock: Clock,
): ViewModel() {

    private val _uiState = MutableStateFlow<EventLogSingleRecordUIState>(EventLogSingleRecordUIState.Loading)
    val uiState: StateFlow<EventLogSingleRecordUIState> = _uiState

    fun loadRecord(eventLogRecord: EventLogRecordPK) = viewModelScope.launch {
        _uiState.value = EventLogSingleRecordUIState.Loading

        val result = eventLogManager.getRecord(eventLogRecord)

        result.onSuccess { record ->
            _uiState.value = EventLogSingleRecordUIState.Success(
                record.toUIModel(),
                "Compartir",
                null,
            )
        }.onFailure {
            _uiState.value = EventLogSingleRecordUIState.Error(R.string.app_name)
        }
    }
}
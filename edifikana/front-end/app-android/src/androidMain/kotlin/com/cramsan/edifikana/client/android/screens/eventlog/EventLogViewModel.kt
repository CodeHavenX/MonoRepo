package com.cramsan.edifikana.client.android.screens.eventlog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cramsan.edifikana.client.android.R
import com.cramsan.edifikana.client.android.managers.EventLogManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class EventLogViewModel @Inject constructor(
    private val eventLogManager: EventLogManager,
): ViewModel() {

    private val _uiState = MutableStateFlow<EventLogUIState>(EventLogUIState.Empty)
    val uiState: StateFlow<EventLogUIState> = _uiState

    fun loadRecords() = viewModelScope.launch {
        _uiState.value = EventLogUIState.Loading
        eventLogManager.getRecords().onSuccess { records ->
            if (records.isEmpty()) {
                _uiState.value = EventLogUIState.Empty
            } else {
                _uiState.value = EventLogUIState.Success(records.map { it.toUIModel() })
            }
        }.onFailure {
            _uiState.value = EventLogUIState.Error(R.string.app_name)
        }
    }
}
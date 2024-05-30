package com.cramsan.edifikana.client.android.features.formlist.records.read

import androidx.lifecycle.viewModelScope
import com.cramsan.edifikana.client.android.features.base.EdifikanaBaseViewModel
import com.cramsan.edifikana.client.android.managers.FormsManager
import com.cramsan.edifikana.lib.firestore.FormRecordPK
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecordReadViewModel @Inject constructor(
    private val formsManager: FormsManager,
    exceptionHandler: CoroutineExceptionHandler,
) : EdifikanaBaseViewModel(exceptionHandler) {

    private val _uiState = MutableStateFlow(
        RecordReadUIState(
            RecordReadUIModel(
                name = "",
                fields = emptyList(),
            ),
            false
        )
    )
    val uiState: StateFlow<RecordReadUIState> = _uiState

    private val _event = MutableSharedFlow<RecordReadEvent>()
    val event: SharedFlow<RecordReadEvent> = _event

    fun loadRecord(recordPK: FormRecordPK) = viewModelScope.launch {
        _uiState.value = _uiState.value.copy(isLoading = true)
        try {
            val result = formsManager.getFormRecord(recordPK).getOrThrow()
            _uiState.value = _uiState.value.copy(content = result.toReadRecordUIModel())
        } finally {
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }
}

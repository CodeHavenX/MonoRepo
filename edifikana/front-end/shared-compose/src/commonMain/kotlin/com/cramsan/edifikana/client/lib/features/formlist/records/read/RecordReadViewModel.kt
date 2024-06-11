package com.cramsan.edifikana.client.lib.features.formlist.records.read

import com.cramsan.edifikana.client.lib.features.base.EdifikanaBaseViewModel
import com.cramsan.edifikana.client.lib.managers.FormsManager
import com.cramsan.edifikana.lib.firestore.FormRecordPK
import com.cramsan.framework.core.DispatcherProvider
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RecordReadViewModel(
    private val formsManager: FormsManager,
    exceptionHandler: CoroutineExceptionHandler,
    dispatcherProvider: DispatcherProvider,
) : EdifikanaBaseViewModel(exceptionHandler, dispatcherProvider) {

    private val _uiState = MutableStateFlow(
        RecordReadUIState(
            RecordReadUIModel(
                name = "",
                fields = emptyList(),
            ),
            false,
            "",
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

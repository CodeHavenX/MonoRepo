package com.cramsan.edifikana.client.lib.features.formlist.records

import com.cramsan.edifikana.client.lib.features.base.EdifikanaBaseViewModel
import com.cramsan.edifikana.client.lib.features.main.MainActivityEvent
import com.cramsan.edifikana.client.lib.features.main.Route
import com.cramsan.edifikana.client.lib.managers.FormsManager
import com.cramsan.edifikana.client.lib.models.FormRecordModel
import com.cramsan.framework.core.DispatcherProvider
import com.cramsan.framework.logging.logE
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RecordsViewModel (
    private val formsManager: FormsManager,
    exceptionHandler: CoroutineExceptionHandler,
    dispatcherProvider: DispatcherProvider,
) : EdifikanaBaseViewModel(exceptionHandler, dispatcherProvider) {

    private val _uiState = MutableStateFlow(
        RecordsUIState(
            content = emptyList(),
            isLoading = false,
            "",
        )
    )
    val uiState: StateFlow<RecordsUIState> = _uiState

    private val _event = MutableSharedFlow<RecordsEvent>()
    val event: SharedFlow<RecordsEvent> = _event

    fun loadRecords() = viewModelScope.launch {
        _uiState.value = _uiState.value.copy(isLoading = true)
        try {
            val result = formsManager.getFormRecords()
            if (result.isFailure) {
                logE(TAG, "Failed to load records", result.exceptionOrNull())
                return@launch
            }

            val records = result.getOrThrow()
            _uiState.value = _uiState.value.copy(content = records.map { it.toUIModel() })
        } catch (e: Throwable) {
            logE(TAG, "Failed to load records", e)
        } finally {
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    fun navigateToRecord(record: FormRecordModel) = viewModelScope.launch {
        _event.emit(
            RecordsEvent.TriggerMainActivityEvent(
                MainActivityEvent.Navigate(Route.toFormRecordReadRoute(requireNotNull(record.formRecordPk)))
            )
        )
    }

    companion object {
        private const val TAG = "RecordsViewModel"
    }
}

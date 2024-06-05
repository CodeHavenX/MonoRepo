package com.cramsan.edifikana.client.android.features.formlist.records

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.cramsan.edifikana.client.android.R
import com.cramsan.edifikana.client.android.features.base.EdifikanaBaseViewModel
import com.cramsan.edifikana.client.android.features.main.MainActivityEvent
import com.cramsan.edifikana.client.android.features.main.Route
import com.cramsan.edifikana.client.android.managers.FormsManager
import com.cramsan.edifikana.client.android.models.FormRecordModel
import com.cramsan.framework.logging.logE
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
class RecordsViewModel @Inject constructor(
    private val formsManager: FormsManager,
    @ApplicationContext
    private val context: Context,
    exceptionHandler: CoroutineExceptionHandler,
) : EdifikanaBaseViewModel(exceptionHandler) {

    private val _uiState = MutableStateFlow(
        RecordsUIState(
            content = emptyList(),
            isLoading = false,
            context.getString(R.string.title_form_record_view)
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

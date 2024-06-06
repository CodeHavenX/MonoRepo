package com.cramsan.edifikana.client.android.features.formlist.entry

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.cramsan.edifikana.client.android.R
import com.cramsan.edifikana.client.android.features.base.EdifikanaBaseViewModel
import com.cramsan.edifikana.client.android.features.main.MainActivityEvent
import com.cramsan.edifikana.client.android.features.main.Route
import com.cramsan.edifikana.client.android.managers.FormsManager
import com.cramsan.edifikana.client.android.managers.WorkContext
import com.cramsan.edifikana.client.lib.managers.mappers.createSubmissionFormRecordModel
import com.cramsan.edifikana.client.lib.models.FormModel
import com.cramsan.edifikana.lib.firestore.FormPK
import com.cramsan.framework.logging.logE
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
class EntryViewModel @Inject constructor(
    private val formsManager: FormsManager,
    private val workContext: WorkContext,
    @ApplicationContext
    private val context: Context,
    exceptionHandler: CoroutineExceptionHandler,
) : EdifikanaBaseViewModel(exceptionHandler) {

    private val _uiState = MutableStateFlow(
        EntryUIState(
            EntryUIModel(
                name = "",
                fields = emptyList(),
                submitAllowed = false,
            ),
            false,
            context.getString(R.string.title_form_entry)
        )
    )
    val uiState: StateFlow<EntryUIState> = _uiState

    private val _event = MutableSharedFlow<EntryEvent>()
    val event: SharedFlow<EntryEvent> = _event

    private val fields = mutableMapOf<String, String>()
    private var formModel: FormModel? = null

    fun loadForm(formPK: FormPK) = viewModelScope.launch {
        _uiState.value = _uiState.value.copy(isLoading = true)
        try {
            val result = formsManager.getForm(formPK).getOrThrow()
            formModel = result
            _uiState.value = _uiState.value.copy(
                content = result.toEntryUIModel(),
            )
        } catch (e: Throwable) {
            logE(TAG, "Failed to load form", e)
        } finally {
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    fun updateField(fieldId: String, value: String) {
        fields[fieldId] = value
        formModel?.let { model ->
            val existsInvalidField = model.fields.any { field ->
                if (field.isRequired) {
                    fields[field.id].isNullOrBlank()
                } else {
                    false
                }
            }
            _uiState.value = _uiState.value.copy(
                content = _uiState.value.content.copy(submitAllowed = !existsInvalidField),
            )
        }
    }

    fun addRecord() = viewModelScope.launch {
        _uiState.value = _uiState.value.copy(isLoading = true)
        try {
            val names = _uiState.value.content.fields.associate { it.fieldId to it.name }
            val formRecord = createSubmissionFormRecordModel(
                requireNotNull(formModel),
                workContext.clock,
                names,
                fields
            )
            val result = formsManager.submitFormRecord(formRecord)
            if (!result.isSuccess) {
                logW(TAG, "Failed to add record: ${formRecord.formRecordPk}", result.exceptionOrNull())
            } else {
                _event.emit(
                    EntryEvent.TriggerMainActivityEvent(
                        MainActivityEvent.Navigate(Route.toFormRecordsRoute())
                    )
                )
            }
        } catch (e: Throwable) {
            logE(TAG, "Failed to add record", e)
        } finally {
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    companion object {
        private const val TAG = "EntryViewModel"
    }
}

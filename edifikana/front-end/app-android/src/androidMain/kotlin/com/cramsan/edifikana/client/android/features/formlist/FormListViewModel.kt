package com.cramsan.edifikana.client.android.features.formlist

import android.content.Context
import com.cramsan.edifikana.client.android.R
import com.cramsan.edifikana.client.android.features.base.EdifikanaBaseViewModel
import com.cramsan.edifikana.client.lib.features.main.MainActivityEvent
import com.cramsan.edifikana.client.lib.features.main.Route
import com.cramsan.edifikana.client.lib.managers.FormsManager
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
class FormListViewModel @Inject constructor(
    private val formsManager: FormsManager,
    @ApplicationContext
    private val context: Context,
    exceptionHandler: CoroutineExceptionHandler,
) : EdifikanaBaseViewModel(exceptionHandler) {

    private val _uiState = MutableStateFlow(FormListUIState(title = context.getString(R.string.title_form_list)))
    val uiState: StateFlow<FormListUIState> = _uiState

    private val _event = MutableSharedFlow<FormListEvent>()
    val event: SharedFlow<FormListEvent> = _event

    fun loadForms() = viewModelScope.launch {
        _uiState.value = _uiState.value.copy(isLoading = true)

        try {
            val forms = formsManager.getForms().getOrThrow().map {
                it.toFormUIModel()
            }
            _uiState.value = FormListUIState(forms = forms, title = context.getString(R.string.title_form_list))
        } finally {
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    fun navigateToForm(formUI: FormUIModel) = viewModelScope.launch {
        _event.emit(
            FormListEvent.TriggerMainActivityEvent(
                MainActivityEvent.Navigate(Route.toFormEntryRoute(formUI.formPk))
            )
        )
    }

    fun navigateToFormRecords() = viewModelScope.launch {
        _event.emit(
            FormListEvent.TriggerMainActivityEvent(
                MainActivityEvent.Navigate(Route.toFormRecordsRoute())
            )
        )
    }
}

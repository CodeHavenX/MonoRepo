package com.cramsan.edifikana.client.lib.features.formlist

import com.cramsan.edifikana.client.lib.features.base.EdifikanaBaseViewModel
import com.cramsan.edifikana.client.lib.features.main.MainActivityEvent
import com.cramsan.edifikana.client.lib.features.main.Route
import com.cramsan.edifikana.client.lib.managers.FormsManager
import com.cramsan.framework.core.DispatcherProvider
import edifikana_lib.Res
import edifikana_lib.title_form_list
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

class FormListViewModel (
    private val formsManager: FormsManager,
    exceptionHandler: CoroutineExceptionHandler,
    dispatcherProvider: DispatcherProvider,
) : EdifikanaBaseViewModel(exceptionHandler, dispatcherProvider) {

    private val _uiState = MutableStateFlow(FormListUIState(title = ""))
    val uiState: StateFlow<FormListUIState> = _uiState

    private val _event = MutableSharedFlow<FormListEvent>()
    val event: SharedFlow<FormListEvent> = _event

    fun loadForms() = viewModelScope.launch {
        _uiState.value = _uiState.value.copy(isLoading = true)

        try {
            val forms = formsManager.getForms().getOrThrow().map {
                it.toFormUIModel()
            }
            _uiState.value = FormListUIState(forms = forms, title = getString(Res.string.title_form_list))
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

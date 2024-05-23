package com.cramsan.edifikana.client.android.features.formlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cramsan.edifikana.client.android.features.main.MainActivityEvent
import com.cramsan.edifikana.client.android.features.main.Route
import com.cramsan.edifikana.client.android.managers.FormsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FormListViewModel @Inject constructor(
    private val formsManager: FormsManager,
) : ViewModel() {
    private val _uiState = MutableStateFlow(FormListUIState())
    val uiState: StateFlow<FormListUIState> = _uiState

    private val _event = MutableSharedFlow<FormListEvent>()
    val event: SharedFlow<FormListEvent> = _event

    fun loadForms() = viewModelScope.launch {
        _uiState.value = _uiState.value.copy(isLoading = true)

        try {
            val forms = formsManager.getForms().getOrThrow().map {
                it.toFormUIModel()
            }
            _uiState.value = FormListUIState(forms = forms)
        } finally {
           _uiState.value = _uiState.value.copy(isLoading = false)
        }

    }

    fun navigateToForm(formUI: FormUIModel) = viewModelScope.launch {
        _event.emit(FormListEvent.TriggerMainActivityEvent(
            MainActivityEvent.Navigate(Route.toFormEntryRoute(formUI.formPk))
        ))
    }

    fun navigateToFormRecords() = viewModelScope.launch {
        _event.emit(FormListEvent.TriggerMainActivityEvent(
            MainActivityEvent.Navigate(Route.toFormRecordsRoute())
        ))
    }
}

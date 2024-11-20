package com.codehavenx.alpaca.frontend.appcore.features.clients.addclient

import com.codehavenx.alpaca.frontend.appcore.features.application.ApplicationEvent
import com.codehavenx.alpaca.frontend.appcore.features.base.AlpacaViewModel
import com.codehavenx.alpaca.frontend.appcore.managers.WorkContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * The ViewModel for the Add Client screen.
 */
class AddClientViewModel(
    workContext: WorkContext,
) : AlpacaViewModel(workContext) {
    private val _uiState = MutableStateFlow(
        AddClientUIState(
            content = AddClientUIModel(""),
            isLoading = false,
        )
    )
    val uiState: StateFlow<AddClientUIState> = _uiState

    private val _event = MutableSharedFlow<AddClientEvent>()
    val event: SharedFlow<AddClientEvent> = _event

    /**
     * Save the client information.
     */
    @Suppress("MagicNumber")
    fun saveClient() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            delay(2000)
            _event.emit(AddClientEvent.TriggerApplicationEvent(ApplicationEvent.NavigateBack()))
        }
    }
}

package com.codehavenx.alpaca.frontend.appcore.features.clients.updateclient

import com.codehavenx.alpaca.frontend.appcore.features.application.ApplicationEvent
import com.codehavenx.alpaca.frontend.appcore.managers.ClientManager
import com.codehavenx.alpaca.frontend.appcore.models.Client
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * The ViewModel for the Update Client screen.
 */
class UpdateClientViewModel(
    private val clientManager: ClientManager,
    dependencies: ViewModelDependencies,
) : BaseViewModel(dependencies) {
    private val _uiState = MutableStateFlow(
        UpdateClientUIState(
            content = null,
            isLoading = false,
        )
    )
    val uiState: StateFlow<UpdateClientUIState> = _uiState

    private val _event = MutableSharedFlow<UpdateClientEvent>()
    val event: SharedFlow<UpdateClientEvent> = _event

    /**
     * Update the client information.
     */
    @Suppress("MagicNumber")
    fun updateClient() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            delay(2000)
            _event.emit(UpdateClientEvent.TriggerApplicationEvent(ApplicationEvent.NavigateBack()))
        }
    }

    /**
     * Load the client information.
     */
    @Suppress("MagicNumber")
    fun loadClient(clientId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            delay(2000)
            _uiState.value = _uiState.value.copy(
                content = clientManager.getClientById(clientId).getOrThrow().toViewUIModel(),
                isLoading = false,
            )
        }
    }
}

/**
 * Convert the Client to the UI Model.
 */
private fun Client.toViewUIModel(): UpdateClientUIModel {
    return UpdateClientUIModel(
        id = id,
        name = name,
        email = email,
        phone = phone,
        address = address,
        city = city,
        state = state,
        zip = zip,
        country = country,
    )
}

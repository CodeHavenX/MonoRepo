package com.codehavenx.alpaca.frontend.appcore.features.clients.viewclient

import com.codehavenx.alpaca.frontend.appcore.features.application.ApplicationEvent
import com.codehavenx.alpaca.frontend.appcore.features.application.Route
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
 * Events for the View Client screen.
 */
class ViewClientViewModel(
    private val clientManager: ClientManager,
    dependencies: ViewModelDependencies,
) : BaseViewModel(dependencies) {

    private val _uiState = MutableStateFlow(
        ViewClientUIState(
            content = null,
            isLoading = true,
        )
    )
    val uiState: StateFlow<ViewClientUIState> = _uiState

    private val _event = MutableSharedFlow<ViewClientEvent>()
    val event: SharedFlow<ViewClientEvent> = _event

    /**
     * Load the client with the given ID.
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

    /**
     * Edit the client with the given ID.
     */
    fun editClient(clientId: String) {
        viewModelScope.launch {
            _event.emit(
                ViewClientEvent.TriggerApplicationEvent(ApplicationEvent.Navigate(Route.updateClient(clientId)))
            )
        }
    }
}

private fun Client.toViewUIModel(): ViewClientUIModel {
    return ViewClientUIModel(
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

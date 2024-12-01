package com.codehavenx.alpaca.frontend.appcore.features.clients.listclients

import com.codehavenx.alpaca.frontend.appcore.features.application.ApplicationEvent
import com.codehavenx.alpaca.frontend.appcore.features.application.Route
import com.codehavenx.alpaca.frontend.appcore.managers.ClientManager
import com.codehavenx.alpaca.frontend.appcore.models.Client
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * The ViewModel for the List Clients screen.
 */
class ListClientViewModel(
    private val clientManager: ClientManager,
    dependencies: ViewModelDependencies,
) : BaseViewModel(dependencies) {

    private val _uiState = MutableStateFlow(
        ListClientUIState(
            users = ClientPageUIModel(emptyList()),
            pagination = ClientPaginationUIModel(
                firstPage = null,
                previousPage = null,
                nextPage = null,
                lastPage = null,
                pages = emptyList(),
            ),
            isLoading = true,
        )
    )
    val uiState: StateFlow<ListClientUIState> = _uiState

    private val _event = MutableSharedFlow<ListClientEvent>()
    val event: SharedFlow<ListClientEvent> = _event

    /**
     * Load the page of clients.
     */
    fun loadPage() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val users = clientManager.getClients().getOrThrow()
            _uiState.value = _uiState.value.copy(
                users = ClientPageUIModel(
                    users.map {
                        it.toUListClientUIModel()
                    }
                ),
                isLoading = false,
            )
        }
    }

    /**
     * Add a new client.
     */
    fun addClient() {
        viewModelScope.launch {
            _event.emit(ListClientEvent.TriggerApplicationEvent(ApplicationEvent.Navigate(Route.addClient())))
        }
    }

    /**
     * Open the client page.
     */
    fun openClientPage(clientId: String) {
        viewModelScope.launch {
            _event.emit(ListClientEvent.TriggerApplicationEvent(ApplicationEvent.Navigate(Route.viewClient(clientId))))
        }
    }
}

private fun Client.toUListClientUIModel(): ClientUIModel {
    return ClientUIModel(
        id = id,
        displayName = name,
    )
}

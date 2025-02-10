package com.codehavenx.alpaca.frontend.appcore.features.clients.listclients

import com.codehavenx.alpaca.frontend.appcore.features.application.ApplicationEvent
import com.codehavenx.alpaca.frontend.appcore.features.application.Route
import com.codehavenx.alpaca.frontend.appcore.managers.ClientManager
import com.codehavenx.alpaca.frontend.appcore.models.Client
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import kotlinx.coroutines.launch

/**
 * The ViewModel for the List Clients screen.
 */
class ListClientViewModel(
    private val clientManager: ClientManager,
    dependencies: ViewModelDependencies,
) : BaseViewModel<ListClientEvent, ListClientUIState>(
    dependencies,
    ListClientUIState.Initial,
    TAG,
) {

    /**
     * Load the page of clients.
     */
    fun loadPage() {
        viewModelScope.launch {
            updateUiState {
                it.copy(isLoading = true)
            }
            val users = clientManager.getClients().getOrThrow()
            updateUiState {
                it.copy(
                    users = ClientPageUIModel(
                        users.map {
                            it.toUListClientUIModel()
                        }
                    ),
                    isLoading = false,
                )
            }
        }
    }

    /**
     * Add a new client.
     */
    fun addClient() {
        viewModelScope.launch {
            emitEvent(ListClientEvent.TriggerApplicationEvent(ApplicationEvent.Navigate(Route.addClient())))
        }
    }

    /**
     * Open the client page.
     */
    fun openClientPage(clientId: String) {
        viewModelScope.launch {
            emitEvent(ListClientEvent.TriggerApplicationEvent(ApplicationEvent.Navigate(Route.viewClient(clientId))))
        }
    }

    companion object {
        private const val TAG = "ListClientViewModel"
    }
}

private fun Client.toUListClientUIModel(): ClientUIModel {
    return ClientUIModel(
        id = id,
        displayName = name,
    )
}

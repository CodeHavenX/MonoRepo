package com.codehavenx.alpaca.frontend.appcore.features.clients.viewclient

import com.codehavenx.alpaca.frontend.appcore.features.application.AlpacaWindowEvent
import com.codehavenx.alpaca.frontend.appcore.features.application.Route
import com.codehavenx.alpaca.frontend.appcore.managers.ClientManager
import com.codehavenx.alpaca.frontend.appcore.models.Client
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Events for the View Client screen.
 */
class ViewClientViewModel(
    private val clientManager: ClientManager,
    dependencies: ViewModelDependencies,
) : BaseViewModel<ViewClientEvent, ViewClientUIState>(
    dependencies,
    ViewClientUIState.Initial,
    TAG,
) {

    /**
     * Load the client with the given ID.
     */
    @Suppress("MagicNumber")
    fun loadClient(clientId: String) {
        viewModelScope.launch {
            updateUiState { it.copy(isLoading = true) }
            delay(2000)
            clientManager.getClientById(clientId).getOrThrow().toViewUIModel()
        }
    }

    /**
     * Edit the client with the given ID.
     */
    fun editClient(clientId: String) {
        viewModelScope.launch {
            emitWindowEvent(
                AlpacaWindowEvent.Navigate(Route.updateClient(clientId))
            )
        }
    }

    companion object {
        private const val TAG = "ViewClientViewModel"
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

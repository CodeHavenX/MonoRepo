package com.codehavenx.alpaca.frontend.appcore.features.clients.updateclient

import com.codehavenx.alpaca.frontend.appcore.features.application.AlpacaApplicationEvent
import com.codehavenx.alpaca.frontend.appcore.managers.ClientManager
import com.codehavenx.alpaca.frontend.appcore.models.Client
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * The ViewModel for the Update Client screen.
 */
@Suppress("UnusedPrivateProperty")
class UpdateClientViewModel(
    private val clientManager: ClientManager,
    dependencies: ViewModelDependencies,
) : BaseViewModel<UpdateClientEvent, UpdateClientUIState>(
    dependencies,
    UpdateClientUIState.Initial,
    TAG,
) {
    /**
     * Update the client information.
     */
    @Suppress("MagicNumber")
    fun updateClient() {
        viewModelScope.launch {
            updateUiState { it.copy(isLoading = true) }
            delay(2000)
            emitApplicationEvent(AlpacaApplicationEvent.NavigateBack)
        }
    }

    /**
     * Load the client information.
     */
    @Suppress("UnusedParameter")
    fun loadClient(clientId: String) = Unit

    companion object {
        private const val TAG = "UpdateClientViewModel"
    }
}

/**
 * Convert the Client to the UI Model.
 */
@Suppress("UnusedPrivateMember")
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

package com.codehavenx.alpaca.frontend.appcore.features.clients.addclient

import com.codehavenx.alpaca.frontend.appcore.features.application.ApplicationEvent
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * The ViewModel for the Add Client screen.
 */
class AddClientViewModel(
    dependencies: ViewModelDependencies,
) : BaseViewModel<AddClientEvent, AddClientUIModel>(
    dependencies,
    AddClientUIModel.Initial,
    TAG,
) {

    /**
     * Save the client information.
     */
    @Suppress("MagicNumber")
    fun saveClient() {
        viewModelScope.launch {
            delay(2000)
            emitEvent(AddClientEvent.TriggerApplicationEvent(ApplicationEvent.NavigateBack))
        }
    }

    companion object {
        private const val TAG = "AddClientViewModel"
    }
}

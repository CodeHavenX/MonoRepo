package com.cramsan.templatereplaceme.client.lib.features.main.menu

import com.cramsan.framework.annotations.FrontendViewModel
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.logging.logD
import com.cramsan.framework.logging.logI
import com.cramsan.templatereplaceme.client.lib.features.window.TemplateReplaceMeWindowsEvent
import com.cramsan.templatereplaceme.client.lib.managers.PingPongManager
import kotlinx.coroutines.launch

/**
 * ViewModel for the Main Menu screen.
 */
@FrontendViewModel
class MainMenuViewModel(dependencies: ViewModelDependencies, private val pingPongManager: PingPongManager) :
    BaseViewModel<MainMenuEvent, MainMenuUIState>(dependencies, MainMenuUIState.Initial, TAG) {
    /**
     * Handle first name value change.
     */
    fun changeFirstNameValue(firstName: String) {
        viewModelCoroutineScope.launch {
            logD(TAG, "First name changed to: $firstName")
            updateUiState {
                it.copy(firstName = firstName)
            }
        }
    }

    /**
     * Handle last name value change.
     */
    fun changeLastNameValue(lastName: String) {
        viewModelCoroutineScope.launch {
            logD(TAG, "Last name changed to: $lastName")
            updateUiState {
                it.copy(lastName = lastName)
            }
        }
    }

    /**
     * Make a Ping request
     */
    fun ping() {
        logI(TAG, "create account called")
        viewModelCoroutineScope.launch {
            updateUiState { it.copy(isLoading = true) }
            val firstName = uiState.value.firstName
            val lastName = uiState.value.lastName
            val pong =
                pingPongManager
                    .ping(
                        firstName = firstName,
                        lastName = lastName,
                    ).onFailure {
                        updateUiState { it.copy(isLoading = false) }
                        emitWindowEvent(
                            TemplateReplaceMeWindowsEvent.ShowSnackbar(
                                message = "Failed to get a pong: ${it.message}",
                            ),
                        )
                    }.onSuccess {
                        updateUiState { it.copy(isLoading = false) }
                        emitWindowEvent(
                            TemplateReplaceMeWindowsEvent.ShowSnackbar(
                                message = "Received pong for $firstName $lastName!",
                            ),
                        )
                    }.getOrThrow()

            logI(TAG, "Pong received: $pong")
        }
    }

    companion object {
        private const val TAG = "MainMenuViewModel"
    }
}

package com.cramsan.templatereplaceme.client.lib.features.main.menu

import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.logging.logD
import com.cramsan.framework.logging.logI
import com.cramsan.templatereplaceme.client.lib.features.window.TemplateReplaceMeWindowsEvent
import com.cramsan.templatereplaceme.client.lib.managers.UserManager
import kotlinx.coroutines.launch

/**
 * ViewModel for the Main Menu screen.
 */
class MainMenuViewModel(dependencies: ViewModelDependencies, private val userManager: UserManager) :
    BaseViewModel<MainMenuEvent, MainMenuUIState>(dependencies, MainMenuUIState.Initial, TAG) {

    /**
     * Handle first name value change.
     */
    fun changeFirstNameValue(firstName: String) {
        viewModelScope.launch {
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
        viewModelScope.launch {
            logD(TAG, "Last name changed to: $lastName")
            updateUiState {
                it.copy(lastName = lastName)
            }
        }
    }

    /**
     * Create account
     */
    fun createAccount() {
        logI(TAG, "create account called")
        viewModelScope.launch {
            updateUiState { it.copy(isLoading = true) }
            val firstName = uiState.value.firstName
            val lastName = uiState.value.lastName
            val newUser = userManager.createUser(
                firstName = firstName,
                lastName = lastName,
            ).onFailure {
                updateUiState { it.copy(isLoading = false) }
                emitWindowEvent(
                    TemplateReplaceMeWindowsEvent.ShowSnackbar(
                        message = "Failed to create user: ${it.message}",
                    ),
                )
            }.onSuccess {
                updateUiState { it.copy(isLoading = false) }
                emitWindowEvent(
                    TemplateReplaceMeWindowsEvent.ShowSnackbar(
                        message = "User $firstName $lastName created successfully!",
                    ),
                )
            }.getOrThrow()

            logI(TAG, "User created: $newUser")
        }
    }

    companion object {
        private const val TAG = "MainMenuViewModel"
    }
}

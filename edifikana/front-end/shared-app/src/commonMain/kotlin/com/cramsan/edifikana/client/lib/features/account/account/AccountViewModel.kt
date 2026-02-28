package com.cramsan.edifikana.client.lib.features.account.account

import com.cramsan.edifikana.client.lib.features.account.AccountDestination
import com.cramsan.edifikana.client.lib.features.window.EdifikanaNavGraphDestination
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import kotlinx.coroutines.launch

/**
 * Account screen view model.
 */
class AccountViewModel(
    private val auth: AuthManager,
    dependencies: ViewModelDependencies,
) : BaseViewModel<AccountEvent, AccountUIState>(dependencies, AccountUIState.Empty, TAG) {

    /**
     * Sign out and navigate out of this screen.
     */
    fun signOut() {
        viewModelScope.launch {
            updateUiState { it.copy(isLoading = true) }
            auth.signOut()
            emitWindowEvent(
                EdifikanaWindowsEvent.NavigateToNavGraph(
                    EdifikanaNavGraphDestination.AuthNavGraphDestination,
                    clearStack = true,
                )
            )
        }
    }

    /**
     * Navigate back to the previous screen.
     * If in edit mode, cancel edit and stay on screen instead.
     */
    fun navigateBack() {
        viewModelScope.launch {
            if (uiState.value.isEditable) {
                cancelEdit()
            } else {
                emitWindowEvent(EdifikanaWindowsEvent.NavigateBack)
            }
        }
    }

    /**
     * Load the user data.
     */
    fun loadUserData() {
        viewModelScope.launch {
            updateUiState { it.copy(isLoading = true) }
            fetchAndApplyUser()
        }
    }

    /**
     * Toggle between edit and save mode.
     * If in edit mode, save changes; otherwise, enter edit mode.
     */
    fun editOrSave() {
        if (uiState.value.isEditable) {
            saveChanges()
        } else {
            viewModelScope.launch {
                updateUiState { it.copy(isEditable = true) }
            }
        }
    }

    /**
     * Cancel edit mode and revert changes.
     */
    fun cancelEdit() {
        viewModelScope.launch {
            updateUiState { it.copy(isLoading = true, isEditable = false) }
            fetchAndApplyUser()
        }
    }

    /**
     * Update the user's first name.
     */
    fun updateFirstName(firstName: String) {
        viewModelScope.launch {
            updateUiState { it.copy(firstName = firstName) }
        }
    }

    /**
     * Update the user's last name.
     */
    fun updateLastName(lastName: String) {
        viewModelScope.launch {
            updateUiState { it.copy(lastName = lastName) }
        }
    }

    /**
     * Update the user's email.
     */
    fun updateEmail(email: String) {
        viewModelScope.launch {
            updateUiState { it.copy(email = email) }
        }
    }

    /**
     * Update the user's phone number.
     */
    fun updatePhoneNumber(phoneNumber: String) {
        viewModelScope.launch {
            updateUiState { it.copy(phoneNumber = phoneNumber) }
        }
    }

    private fun saveChanges() {
        viewModelScope.launch {
            updateUiState { it.copy(isLoading = true) }
            auth.updateUser(
                firstName = uiState.value.firstName,
                lastName = uiState.value.lastName,
                email = uiState.value.email,
                phoneNumber = uiState.value.phoneNumber,
            ).onFailure {
                updateUiState { it.copy(isLoading = false) }
                emitWindowEvent(
                    EdifikanaWindowsEvent.ShowSnackbar(
                        "Failed to update account information. Please try again."
                    )
                )
                return@launch
            }
            updateUiState {
                it.copy(
                    isLoading = false,
                    isEditable = false,
                )
            }
            emitWindowEvent(
                EdifikanaWindowsEvent.ShowSnackbar(
                    "Account information updated successfully."
                )
            )
        }
    }

    /**
     * Navigate to the change password screen.
     */
    fun editPassword() {
        viewModelScope.launch {
            emitWindowEvent(
                EdifikanaWindowsEvent.NavigateToScreen(
                    AccountDestination.ChangePasswordDestination
                )
            )
        }
    }

    /**
     * Helper method for fetching the user data and applying it to the UI state.
     */
    private suspend fun fetchAndApplyUser() {
        val response = auth.getUser()
        if (response.isFailure) {
            updateUiState { it.copy(isLoading = false) }
            return
        }
        val user = response.getOrThrow()
        updateUiState {
            it.copy(
                isLoading = false,
                firstName = user.firstName,
                lastName = user.lastName,
                email = user.email,
                phoneNumber = user.phoneNumber,
                isPasswordSet = user.authMetadata?.isPasswordSet == true,
            )
        }
    }

    companion object {
        private const val TAG = "AccountViewModel"
    }
}

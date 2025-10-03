package com.cramsan.edifikana.client.lib.features.management.addprimaryemployee

import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.core.compose.resources.StringProvider
import com.cramsan.framework.utils.loginvalidation.validateEmail
import edifikana_lib.Res
import edifikana_lib.text_there_was_an_error_processing_request
import kotlinx.coroutines.launch

/**
 * ViewModel for the AddPrimaryEmployee screen.
 **/
class AddPrimaryEmployeeViewModel(
    dependencies: ViewModelDependencies,
    private val authManager: AuthManager,
    private val stringProvider: StringProvider,
) : BaseViewModel<AddPrimaryEmployeeEvent, AddPrimaryEmployeeUIState>(
    dependencies,
    AddPrimaryEmployeeUIState.Initial,
    TAG,
) {

    /**
     * Trigger the back event.
     */
    fun navigateBack() {
        viewModelScope.launch {
            emitWindowEvent(EdifikanaWindowsEvent.NavigateBack)
        }
    }

    /**
     * Invite employee member.
     */
    fun invite(email: String) {
        viewModelScope.launch {
            val errorMessages = validateEmail(email.trim())
            if (errorMessages.isNotEmpty()) {
                updateUiState {
                    it.copy(
                        isLoading = false,
                        errorMessage = errorMessages.first()
                    )
                }
                return@launch
            }
            updateUiState { it.copy(isLoading = true, errorMessage = null) }

            val result = authManager.inviteEmployee(email)

            if (result.isFailure) {
                val errorMessage = stringProvider.getString(Res.string.text_there_was_an_error_processing_request)
                updateUiState {
                    it.copy(
                        isLoading = false,
                        errorMessage = errorMessage,
                    )
                }
            } else {
                emitWindowEvent(
                    EdifikanaWindowsEvent.ShowSnackbar(
                        "Email was sent to $email to join this organization."
                    )
                )
                emitWindowEvent(EdifikanaWindowsEvent.NavigateBack)
            }
        }
    }

    companion object {
        private const val TAG = "AddPrimaryEmployeeViewModel"
    }
}

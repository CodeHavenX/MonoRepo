package com.cramsan.edifikana.client.lib.features.admin.addprimarystaff

import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationEvent
import com.cramsan.edifikana.client.lib.managers.StaffManager
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.utils.loginvalidation.validateEmail
import edifikana_lib.Res
import edifikana_lib.text_there_was_an_error_processing_request
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

/**
 * ViewModel for the AddPrimaryStaff screen.
 **/
class AddPrimaryStaffViewModel(
    dependencies: ViewModelDependencies,
    private val staffManager: StaffManager,
) : BaseViewModel<AddPrimaryStaffEvent, AddPrimaryStaffUIState>(
    dependencies,
    AddPrimaryStaffUIState.Initial,
    TAG,
) {

    /**
     * Trigger the back event.
     */
    fun navigateBack() {
        viewModelScope.launch {
            emitApplicationEvent(EdifikanaApplicationEvent.NavigateBack)
        }
    }

    /**
     * Invite staff member.
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

            val result = staffManager.inviteStaff(email)

            if (result.isFailure) {
                val errorMessage = getString(Res.string.text_there_was_an_error_processing_request)
                updateUiState {
                    it.copy(
                        isLoading = false,
                        errorMessage = errorMessage,
                    )
                }
            } else {
                emitApplicationEvent(
                    EdifikanaApplicationEvent.ShowSnackbar(
                        "Email was sent to $email to join this organization."
                    )
                )
                emitApplicationEvent(EdifikanaApplicationEvent.NavigateBack)
            }
        }
    }

    companion object {
        private const val TAG = "AddPrimaryStaffViewModel"
    }
}

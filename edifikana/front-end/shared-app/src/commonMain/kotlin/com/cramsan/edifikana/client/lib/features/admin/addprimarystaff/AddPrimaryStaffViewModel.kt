package com.cramsan.edifikana.client.lib.features.admin.addprimarystaff

import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationEvent
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.utils.loginvalidation.validateEmail
import kotlinx.coroutines.launch

/**
 * ViewModel for the AddPrimaryStaff screen.
 **/
class AddPrimaryStaffViewModel(
    dependencies: ViewModelDependencies,
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
            emitEvent(AddPrimaryStaffEvent.TriggerApplicationEvent(EdifikanaApplicationEvent.NavigateBack))
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
                updateUiState { it.copy(isLoading = true, errorMessage = null) }
                emitEvent(AddPrimaryStaffEvent.TriggerApplicationEvent(EdifikanaApplicationEvent.NavigateBack))
            }
        }
    }

    companion object {
        private const val TAG = "AddPrimaryStaffViewModel"
    }
}

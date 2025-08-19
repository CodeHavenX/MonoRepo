package com.cramsan.edifikana.client.lib.features.management.staff

import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.StaffManager
import com.cramsan.edifikana.client.lib.models.StaffModel
import com.cramsan.edifikana.client.lib.models.fullName
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.edifikana.lib.model.StaffRole
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import kotlinx.coroutines.launch

/**
 * ViewModel for the Staff screen.
 **/
class StaffViewModel(
    dependencies: ViewModelDependencies,
    private val staffManager: StaffManager,
) : BaseViewModel<StaffEvent, StaffUIState>(
    dependencies,
    StaffUIState.Initial,
    TAG,
) {

    /**
     * Load the staff with the given [staffId].
     */
    fun loadStaff(staffId: StaffId) {
        viewModelScope.launch {
            val staffResult = staffManager.getStaff(staffId)

            if (staffResult.isFailure) {
                updateUiState {
                    it.copy(
                        title = "",
                        isLoading = false,
                    )
                }
                return@launch
            }
            val staff = staffResult.getOrThrow()
            val editable = staff.email == null || staff.email.isEmpty()

            updateUiState {
                it.copy(
                    staffId = staffId,
                    title = staff.fullName(),
                    isLoading = false,
                    idType = staff.idType,
                    firstName = staff.firstName,
                    lastName = staff.lastName,
                    role = staff.role,
                    isEditable = editable,
                    canSave = if (editable) {
                        false
                    } else {
                        null
                    },
                )
            }
        }
    }

    /**
     * Trigger the back event.
     */
    fun onBackSelected() {
        viewModelScope.launch {
            emitWindowEvent(EdifikanaWindowsEvent.NavigateBack)
        }
    }

    /**
     * Save the staff details.
     */
    fun onSaveClicked() {
        uiState.value.staffId?.let { staffId ->
            viewModelScope.launch {
                updateUiState { it.copy(isLoading = true) }
                staffManager.updateStaff(
                    StaffModel.UpdateStaffRequest(
                        staffId = staffId,
                        firstName = uiState.value.firstName ?: "",
                        lastName = uiState.value.lastName ?: "",
                        role = uiState.value.role ?: StaffRole.SECURITY_COVER,
                    )
                ).onFailure {
                    updateUiState {
                        it.copy(isLoading = false)
                    }
                }.onSuccess {
                    emitWindowEvent(EdifikanaWindowsEvent.ShowSnackbar("Staff updated successfully"))
                    emitWindowEvent(EdifikanaWindowsEvent.NavigateBack)
                }
            }
        }
    }

    /**
     * Update the first name of the staff.
     */
    fun updateFirstName(firstName: String) {
        viewModelScope.launch {
            updateUiState {
                it.copy(firstName = firstName, canSave = true)
            }
        }
    }

    /**
     * Update the last name of the staff.
     */
    fun updateLastName(lastName: String) {
        viewModelScope.launch {
            updateUiState {
                it.copy(lastName = lastName, canSave = true)
            }
        }
    }

    /**
     * Update the role of the staff.
     */
    fun updateRole(role: StaffRole) {
        viewModelScope.launch {
            updateUiState {
                it.copy(role = role, canSave = true)
            }
        }
    }

    companion object {
        private const val TAG = "StaffViewModel"
    }
}

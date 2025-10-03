package com.cramsan.edifikana.client.lib.features.management.employee

import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.EmployeeManager
import com.cramsan.edifikana.client.lib.models.EmployeeModel
import com.cramsan.edifikana.client.lib.models.fullName
import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.edifikana.lib.model.EmployeeRole
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import kotlinx.coroutines.launch

/**
 * ViewModel for the Employee screen.
 **/
class EmployeeViewModel(
    dependencies: ViewModelDependencies,
    private val employeeManager: EmployeeManager,
) : BaseViewModel<EmployeeEvent, EmployeeUIState>(
    dependencies,
    EmployeeUIState.Initial,
    TAG,
) {

    /**
     * Load the employee with the given [employeeId].
     */
    fun loadEmployee(employeeId: EmployeeId) {
        viewModelScope.launch {
            val employeeResult = employeeManager.getEmployee(employeeId)

            if (employeeResult.isFailure) {
                updateUiState {
                    it.copy(
                        title = "",
                        isLoading = false,
                    )
                }
                return@launch
            }
            val employee = employeeResult.getOrThrow()
            val editable = employee.email == null || employee.email.isEmpty()

            updateUiState {
                it.copy(
                    employeeId = employeeId,
                    title = employee.fullName(),
                    isLoading = false,
                    idType = employee.idType,
                    firstName = employee.firstName,
                    lastName = employee.lastName,
                    role = employee.role,
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
     * Save the employee details.
     */
    fun onSaveClicked() {
        uiState.value.employeeId?.let { empId ->
            viewModelScope.launch {
                updateUiState { it.copy(isLoading = true) }
                employeeManager.updateEmployee(
                    EmployeeModel.UpdateEmployeeRequest(
                        employeeId = empId,
                        firstName = uiState.value.firstName ?: "",
                        lastName = uiState.value.lastName ?: "",
                        role = uiState.value.role ?: EmployeeRole.SECURITY_COVER,
                    )
                ).onFailure {
                    updateUiState {
                        it.copy(isLoading = false)
                    }
                }.onSuccess {
                    emitWindowEvent(EdifikanaWindowsEvent.ShowSnackbar("Employee updated successfully"))
                    emitWindowEvent(EdifikanaWindowsEvent.NavigateBack)
                }
            }
        }
    }

    /**
     * Update the first name of the employee.
     */
    fun updateFirstName(firstName: String) {
        viewModelScope.launch {
            updateUiState {
                it.copy(firstName = firstName, canSave = true)
            }
        }
    }

    /**
     * Update the last name of the employee.
     */
    fun updateLastName(lastName: String) {
        viewModelScope.launch {
            updateUiState {
                it.copy(lastName = lastName, canSave = true)
            }
        }
    }

    /**
     * Update the role of the employee.
     */
    fun updateRole(role: EmployeeRole) {
        viewModelScope.launch {
            updateUiState {
                it.copy(role = role, canSave = true)
            }
        }
    }

    companion object {
        private const val TAG = "EmployeeViewModel"
    }
}

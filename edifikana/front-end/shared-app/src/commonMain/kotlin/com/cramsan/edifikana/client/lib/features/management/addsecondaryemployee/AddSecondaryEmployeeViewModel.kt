package com.cramsan.edifikana.client.lib.features.management.addsecondaryemployee

import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.EmployeeManager
import com.cramsan.edifikana.client.lib.managers.PropertyManager
import com.cramsan.edifikana.client.lib.models.EmployeeModel
import com.cramsan.edifikana.lib.model.EmployeeRole
import com.cramsan.edifikana.lib.model.IdType
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.core.compose.resources.StringProvider
import edifikana_lib.Res
import edifikana_lib.text_please_complete_fields
import edifikana_lib.text_there_was_an_error_processing_request
import edifikana_lib.title_timecard_add_employee
import kotlinx.coroutines.launch

/**
 * ViewModel for the AddSecondary screen.
 **/
class AddSecondaryEmployeeViewModel(
    private val employeeManager: EmployeeManager,
    private val propertyManager: PropertyManager,
    private val stringProvider: StringProvider,
    dependencies: ViewModelDependencies,
) : BaseViewModel<AddSecondaryEmployeeEvent, AddSecondaryEmployeeUIState>(
    dependencies,
    AddSecondaryEmployeeUIState.Initial,
    TAG,
) {

    /**
     * Trigger the back event.
     */
    fun onBackSelected() {
        viewModelScope.launch {
            emitWindowEvent(EdifikanaWindowsEvent.NavigateBack)
        }
    }

    /**
     * Save employee member.
     */
    @Suppress("ComplexCondition")
    fun saveEmployee(
        id: String?,
        idType: IdType?,
        name: String?,
        lastName: String?,
        role: EmployeeRole?,
    ) = viewModelScope.launch {
        if (
            id.isNullOrBlank() ||
            idType == null ||
            name.isNullOrBlank() ||
            lastName.isNullOrBlank() ||
            role == null
        ) {
            emitWindowEvent(
                // TODO: Add compose resource loading
                EdifikanaWindowsEvent.ShowSnackbar(stringProvider.getString(Res.string.text_please_complete_fields))
            )
            return@launch
        }

        val state = AddSecondaryEmployeeUIState(
            isLoading = true,
            stringProvider.getString(Res.string.title_timecard_add_employee)
        )
        updateUiState { state }

        val result = employeeManager.addEmployee(
            EmployeeModel.CreateEmployeeRequest(
                idType = idType,
                firstName = name.trim(),
                lastName = lastName.trim(),
                role = role,
                propertyId = propertyManager.activeProperty().value!!
            )
        )

        if (result.isFailure || result.isFailure) {
            emitWindowEvent(
                // TODO: Add compose resource loading
                EdifikanaWindowsEvent.ShowSnackbar(
                    stringProvider.getString(Res.string.text_there_was_an_error_processing_request)
                )
            )
        } else {
            emitWindowEvent(
                EdifikanaWindowsEvent.NavigateBack
            )
        }
    }

    companion object {
        private const val TAG = "AddSecondaryViewModel"
    }
}

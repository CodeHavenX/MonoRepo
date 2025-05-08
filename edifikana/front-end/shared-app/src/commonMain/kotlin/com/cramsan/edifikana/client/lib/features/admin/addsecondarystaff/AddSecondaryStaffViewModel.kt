package com.cramsan.edifikana.client.lib.features.admin.addsecondarystaff

import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationEvent
import com.cramsan.edifikana.client.lib.managers.PropertyManager
import com.cramsan.edifikana.client.lib.managers.StaffManager
import com.cramsan.edifikana.client.lib.models.StaffModel
import com.cramsan.edifikana.lib.model.IdType
import com.cramsan.edifikana.lib.model.StaffRole
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.core.compose.resources.StringProvider
import edifikana_lib.Res
import edifikana_lib.text_please_complete_fields
import edifikana_lib.text_there_was_an_error_processing_request
import edifikana_lib.title_timecard_add_staff
import kotlinx.coroutines.launch

/**
 * ViewModel for the AddSecondary screen.
 **/
class AddSecondaryStaffViewModel(
    private val staffManager: StaffManager,
    private val propertyManager: PropertyManager,
    private val stringProvider: StringProvider,
    dependencies: ViewModelDependencies,
) : BaseViewModel<AddSecondaryStaffEvent, AddSecondaryStaffUIState>(
    dependencies,
    AddSecondaryStaffUIState.Initial,
    TAG,
) {

    /**
     * Trigger the back event.
     */
    fun onBackSelected() {
        viewModelScope.launch {
            emitApplicationEvent(EdifikanaApplicationEvent.NavigateBack)
        }
    }

    /**
     * Save staff member.
     */
    @Suppress("ComplexCondition")
    fun saveStaff(
        id: String?,
        idType: IdType?,
        name: String?,
        lastName: String?,
        role: StaffRole?,
    ) = viewModelScope.launch {
        if (
            id.isNullOrBlank() ||
            idType == null ||
            name.isNullOrBlank() ||
            lastName.isNullOrBlank() ||
            role == null
        ) {
            emitApplicationEvent(
                // TODO: Add compose resource loading
                EdifikanaApplicationEvent.ShowSnackbar(stringProvider.getString(Res.string.text_please_complete_fields))
            )
            return@launch
        }

        val state = AddSecondaryStaffUIState(
            isLoading = true,
            stringProvider.getString(Res.string.title_timecard_add_staff)
        )
        updateUiState { state }

        val result = staffManager.addStaff(
            StaffModel.CreateStaffRequest(
                idType = idType,
                firstName = name.trim(),
                lastName = lastName.trim(),
                role = role,
                propertyId = propertyManager.activeProperty().value!!
            )
        )

        if (result.isFailure || result.isFailure) {
            emitApplicationEvent(
                // TODO: Add compose resource loading
                EdifikanaApplicationEvent.ShowSnackbar(
                    stringProvider.getString(Res.string.text_there_was_an_error_processing_request)
                )
            )
        } else {
            emitApplicationEvent(
                EdifikanaApplicationEvent.NavigateBack
            )
        }
    }

    companion object {
        private const val TAG = "AddSecondaryViewModel"
    }
}

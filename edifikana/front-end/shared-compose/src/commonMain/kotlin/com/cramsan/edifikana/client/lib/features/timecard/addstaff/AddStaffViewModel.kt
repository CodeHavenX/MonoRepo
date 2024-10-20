package com.cramsan.edifikana.client.lib.features.timecard.addstaff

import com.cramsan.edifikana.client.lib.features.base.EdifikanaBaseViewModel
import com.cramsan.edifikana.client.lib.features.main.MainActivityEvent
import com.cramsan.edifikana.client.lib.managers.PropertyManager
import com.cramsan.edifikana.client.lib.managers.StaffManager
import com.cramsan.edifikana.client.lib.models.StaffModel
import com.cramsan.edifikana.lib.model.IdType
import com.cramsan.edifikana.lib.model.StaffRole
import com.cramsan.framework.core.DispatcherProvider
import edifikana_lib.Res
import edifikana_lib.text_please_complete_fields
import edifikana_lib.text_there_was_an_error_processing_request
import edifikana_lib.title_timecard_add_staff
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

/**
 * View model for adding a staff member.
 */
class AddStaffViewModel constructor(
    private val staffManager: StaffManager,
    private val propertyManager: PropertyManager,
    exceptionHandler: CoroutineExceptionHandler,
    dispatcherProvider: DispatcherProvider,
) : EdifikanaBaseViewModel(exceptionHandler, dispatcherProvider) {

    private val _uiState = MutableStateFlow(
        AddStaffUIState(false, "")
    )
    val uiState: StateFlow<AddStaffUIState> = _uiState

    private val _event = MutableSharedFlow<AddStaffEvent>()

    /**
     * Event flow.
     */
    val event: SharedFlow<AddStaffEvent> = _event

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
            _event.emit(
                AddStaffEvent.TriggerMainActivityEvent(
                    // TODO: Add compose resource loading
                    MainActivityEvent.ShowSnackbar(getString(Res.string.text_please_complete_fields))
                )
            )
            return@launch
        }

        _uiState.value = AddStaffUIState(
            isLoading = true,
            getString(Res.string.title_timecard_add_staff)
        )

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
            _event.emit(
                AddStaffEvent.TriggerMainActivityEvent(
                    // TODO: Add compose resource loading
                    MainActivityEvent.ShowSnackbar(
                        getString(Res.string.text_there_was_an_error_processing_request)
                    )
                )
            )
        } else {
            _event.emit(
                AddStaffEvent.TriggerMainActivityEvent(
                    MainActivityEvent.NavigateBack()
                )
            )
        }
    }
}

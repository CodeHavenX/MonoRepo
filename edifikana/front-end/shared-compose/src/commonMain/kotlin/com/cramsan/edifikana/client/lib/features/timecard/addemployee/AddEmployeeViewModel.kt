package com.cramsan.edifikana.client.lib.features.timecard.addemployee

import com.cramsan.edifikana.client.lib.features.base.EdifikanaBaseViewModel
import com.cramsan.edifikana.client.lib.features.main.MainActivityEvent
import com.cramsan.edifikana.client.lib.managers.EmployeeManager
import com.cramsan.edifikana.client.lib.models.EmployeeModel
import com.cramsan.edifikana.lib.firestore.EmployeeRole
import com.cramsan.edifikana.lib.model.IdType
import com.cramsan.framework.core.DispatcherProvider
import edifikana_lib.Res
import edifikana_lib.text_please_complete_fields
import edifikana_lib.text_there_was_an_error_processing_request
import edifikana_lib.title_timecard_add_employee
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

class AddEmployeeViewModel constructor(
    private val employeeManager: EmployeeManager,
    exceptionHandler: CoroutineExceptionHandler,
    dispatcherProvider: DispatcherProvider,
) : EdifikanaBaseViewModel(exceptionHandler, dispatcherProvider) {

    private val _uiState = MutableStateFlow(
        AddEmployeeUIState(false, "")
    )
    val uiState: StateFlow<AddEmployeeUIState> = _uiState

    private val _event = MutableSharedFlow<AddEmployeeEvent>()
    val event: SharedFlow<AddEmployeeEvent> = _event

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
            _event.emit(
                AddEmployeeEvent.TriggerMainActivityEvent(
                    // TODO: Add compose resource loading
                    MainActivityEvent.ShowSnackbar(getString(Res.string.text_please_complete_fields))
                )
            )
            return@launch
        }

        _uiState.value = AddEmployeeUIState(
            isLoading = true,
            getString(Res.string.title_timecard_add_employee)
        )

        val result = employeeManager.addEmployee(
            EmployeeModel(
                employeePK = null,
                id = id.trim(),
                idType = idType,
                name = name.trim(),
                lastName = lastName.trim(),
                role = role,
            )
        )

        if (result.isFailure || result.isFailure) {
            _event.emit(
                AddEmployeeEvent.TriggerMainActivityEvent(
                    // TODO: Add compose resource loading
                    MainActivityEvent.ShowSnackbar(
                        getString(Res.string.text_there_was_an_error_processing_request)
                    )
                )
            )
        } else {
            _event.emit(
                AddEmployeeEvent.TriggerMainActivityEvent(
                    MainActivityEvent.NavigateBack()
                )
            )
        }
    }
}

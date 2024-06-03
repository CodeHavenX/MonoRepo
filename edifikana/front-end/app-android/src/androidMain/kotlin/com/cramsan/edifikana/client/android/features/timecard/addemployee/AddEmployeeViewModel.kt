package com.cramsan.edifikana.client.android.features.timecard.addemployee

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.cramsan.edifikana.client.android.R
import com.cramsan.edifikana.client.android.features.base.EdifikanaBaseViewModel
import com.cramsan.edifikana.client.android.features.main.MainActivityEvent
import com.cramsan.edifikana.client.android.managers.EmployeeManager
import com.cramsan.edifikana.client.android.models.EmployeeModel
import com.cramsan.edifikana.lib.firestore.EmployeePK
import com.cramsan.edifikana.lib.firestore.EmployeeRole
import com.cramsan.edifikana.lib.firestore.IdType
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEmployeeViewModel @Inject constructor(
    private val employeeManager: EmployeeManager,
    @ApplicationContext
    private val applicationContext: Context,
    exceptionHandler: CoroutineExceptionHandler,
) : EdifikanaBaseViewModel(exceptionHandler) {

    private val _uiState = MutableStateFlow(
        AddEmployeeUIState(false, applicationContext.getString(R.string.title_timecard_add_employee))
    )
    val uiState: StateFlow<AddEmployeeUIState> = _uiState

    private val _event = MutableSharedFlow<AddEmployeeEvent>()
    val event: SharedFlow<AddEmployeeEvent> = _event

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
                    MainActivityEvent.ShowSnackbar(applicationContext.getString(R.string.text_please_complete_fields))
                )
            )
            return@launch
        }

        _uiState.value = AddEmployeeUIState(
            isLoading = true,
            applicationContext.getString(R.string.title_timecard_add_employee)
        )

        val result = employeeManager.addEmployee(
            EmployeeModel(
                employeePK = EmployeePK(""),
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
                        applicationContext.getString(R.string.text_there_was_an_error_processing_request)
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

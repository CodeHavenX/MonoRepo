package com.cramsan.edifikana.client.android.screens.clockinout.add

import android.graphics.Bitmap
import android.net.Uri
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cramsan.edifikana.client.android.R
import com.cramsan.edifikana.client.android.managers.EmployeeManager
import com.cramsan.edifikana.client.android.managers.StorageManager
import com.cramsan.edifikana.client.android.managers.TimeCardManager
import com.cramsan.edifikana.client.android.utils.downscaleImage
import com.cramsan.edifikana.lib.firestore.Employee
import com.cramsan.edifikana.lib.firestore.EmployeePK
import com.cramsan.edifikana.lib.firestore.EmployeeRole
import com.cramsan.edifikana.lib.firestore.IdType
import com.cramsan.edifikana.lib.firestore.TimeCardEventType
import com.cramsan.edifikana.lib.firestore.TimeCardRecord
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

@HiltViewModel
class ClockInOutSingleEmployeeViewModel @Inject constructor(
    private val employeeManager: EmployeeManager,
): ViewModel() {

    private val _uiState = MutableStateFlow<ClockInOutSingleEmployeeUIState>(ClockInOutSingleEmployeeUIState.Success)
    val uiState: StateFlow<ClockInOutSingleEmployeeUIState> = _uiState

    private val _event = MutableStateFlow<ClockInOutSingleEmployeeUIEvent>(ClockInOutSingleEmployeeUIEvent.Noop)
    val event: StateFlow<ClockInOutSingleEmployeeUIEvent> = _event

    fun saveEmployee(
        id: String?,
        idType: IdType?,
        name: String?,
        lastName: String?,
        role: EmployeeRole?,
    ) = viewModelScope.launch {
        _uiState.value = ClockInOutSingleEmployeeUIState.Loading

        val result = employeeManager.addEmployee(Employee(
            id = id,
            idType = idType,
            name = name,
            lastName = lastName,
            role = role,
        ))

        if (result.isFailure || result.isFailure) {
            _uiState.value = ClockInOutSingleEmployeeUIState.Error(R.string.app_name)
        } else {
            _event.value = ClockInOutSingleEmployeeUIEvent.UploadCompleted
        }
    }
}
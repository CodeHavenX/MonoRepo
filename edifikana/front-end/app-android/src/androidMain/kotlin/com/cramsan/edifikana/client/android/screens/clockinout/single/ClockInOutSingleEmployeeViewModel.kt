package com.cramsan.edifikana.client.android.screens.clockinout.single

import android.net.Uri
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cramsan.edifikana.client.android.R
import com.cramsan.edifikana.client.android.managers.EmployeeManager
import com.cramsan.edifikana.client.android.managers.StorageManager
import com.cramsan.edifikana.client.android.managers.TimeCardManager
import com.cramsan.edifikana.lib.firestore.Employee
import com.cramsan.edifikana.lib.firestore.EmployeePK
import com.cramsan.edifikana.lib.firestore.TimeCardEventType
import com.cramsan.edifikana.lib.firestore.TimeCardRecord
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

@HiltViewModel
class ClockInOutSingleEmployeeViewModel @Inject constructor(
    private val employeeManager: EmployeeManager,
    private val timeCardManager: TimeCardManager,
    private val storageManager: StorageManager,
    private val clock: Clock,
): ViewModel() {

    private val _uiState = MutableStateFlow<ClockInOutSingleEmployeeUIState>(ClockInOutSingleEmployeeUIState.Empty)
    val uiState: StateFlow<ClockInOutSingleEmployeeUIState> = _uiState

    private val _event = MutableStateFlow<ClockInOutSingleEmployeeUIEvent>(ClockInOutSingleEmployeeUIEvent.Noop)
    val event: StateFlow<ClockInOutSingleEmployeeUIEvent> = _event

    private var eventType: TimeCardEventType? = null
    private var employee: Employee? = null

    fun loadEmployee(employeePK: EmployeePK) = viewModelScope.launch {
        _uiState.value = ClockInOutSingleEmployeeUIState.Loading

        val employeeTask = async { employeeManager.getEmployee(employeePK) }
        val recordsTask = async { timeCardManager.getRecords(employeePK) }

        val employeeResult = employeeTask.await()
        val recordsResult = recordsTask.await()

        if (employeeResult.isFailure || recordsResult.isFailure) {
            _uiState.value = ClockInOutSingleEmployeeUIState.Error(R.string.app_name)
            return@launch
        } else {
            employee = employeeResult.getOrNull()
            _uiState.value = ClockInOutSingleEmployeeUIState.Success(
                employeeResult.getOrThrow().toUIModel(),
                recordsResult.getOrThrow().map { it.toUIModel() },
            )
        }
    }

    fun share() = viewModelScope.launch {
        val state = uiState.value as? ClockInOutSingleEmployeeUIState.Success ?: return@launch

        val record = state.records.firstOrNull() ?: return@launch

        val imageUri = record.imageRed?.let {
            val res = storageManager.downloadFile(it)

            if (res.isSuccess) {
                res.getOrNull()?.toUri()
            } else {
                null
            }
        }
        val localEventType = record.eventTypeEnum
        _event.value = ClockInOutSingleEmployeeUIEvent.ShareEvent(formatShareMessage(employee, localEventType), imageUri)
    }

    fun setEventType(eventType: TimeCardEventType) {
        this.eventType = eventType
    }

    private fun clearEventType() {
        this.eventType = null
    }

    fun onClockEventSelected(eventType: TimeCardEventType) {
        setEventType(eventType)
        // TODO: Set the filename
        _event.value = ClockInOutSingleEmployeeUIEvent.OnAddRecordRequested(eventType.toString())
    }

    fun recordClockEvent(employeePk: EmployeePK, photoUri: Uri) = viewModelScope.launch {
        val timeCardEventType = eventType ?: return@launch

        _uiState.value = ClockInOutSingleEmployeeUIState.Loading

        val file = photoUri.toFile()
        /*
        val scaledDownImage = downscaleImage(file.absolutePath, 960, 1280)
        val baos = ByteArrayOutputStream()
        scaledDownImage.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val imagePhotoResult = storageManager.uploadFile(data, file.name)
         */
        val imagePhotoResult = storageManager.uploadFile(file.toUri())

        if (imagePhotoResult.isFailure) {
            _uiState.value = ClockInOutSingleEmployeeUIState.Error(R.string.app_name)
            clearEventType()
            return@launch
        }

        timeCardManager.addRecord(TimeCardRecord(
            employeePk.documentPath,
            timeCardEventType,
            clock.now().epochSeconds,
            imageUrl = imagePhotoResult.getOrThrow(),
        )).onSuccess {
            _event.value = ClockInOutSingleEmployeeUIEvent.ShareEvent(formatShareMessage(employee, eventType), photoUri)
            loadEmployee(employeePk)
            clearEventType()
        }.onFailure {
            _uiState.value = ClockInOutSingleEmployeeUIState.Error(R.string.app_name)
            clearEventType()
        }
    }

    private fun formatShareMessage(employee: Employee?, record: TimeCardEventType?): String {
        return "Entrada de ${employee?.name} a las ${record}"
    }
}
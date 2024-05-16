package com.cramsan.edifikana.client.android.features.timecard.viewemployee

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cramsan.edifikana.client.android.features.main.MainActivityEvent
import com.cramsan.edifikana.client.android.managers.EmployeeManager
import com.cramsan.edifikana.client.android.managers.StorageService
import com.cramsan.edifikana.client.android.managers.TimeCardManager
import com.cramsan.edifikana.client.android.models.EmployeeModel
import com.cramsan.edifikana.client.android.models.StorageRef
import com.cramsan.edifikana.client.android.models.TimeCardRecordModel
import com.cramsan.edifikana.client.android.models.fullName
import com.cramsan.edifikana.client.lib.eventTypeFriendlyName
import com.cramsan.edifikana.client.lib.toFriendlyDateTime
import com.cramsan.edifikana.lib.firestore.EmployeePK
import com.cramsan.edifikana.lib.firestore.TimeCardEventType
import com.cramsan.edifikana.lib.firestore.TimeCardRecordPK
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

@HiltViewModel
class ViewEmployeeViewModel @Inject constructor(
    private val employeeManager: EmployeeManager,
    private val timeCardManager: TimeCardManager,
    private val storageService: StorageService,
    private val clock: Clock,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ViewEmployeeUIState(
            true,
            null,
            emptyList()
        )
    )
    val uiState: StateFlow<ViewEmployeeUIState> = _uiState

    private val _event = MutableSharedFlow<ViewEmployeeEvent>()
    val event: SharedFlow<ViewEmployeeEvent> = _event

    private var eventType: TimeCardEventType? = null
    private var employee: EmployeeModel? = null

    fun loadEmployee(employeePK: EmployeePK) = viewModelScope.launch {
        _uiState.value = _uiState.value.copy(isLoading = true)

        val employeeTask = async { employeeManager.getEmployee(employeePK) }
        val recordsTask = async { timeCardManager.getRecords(employeePK) }

        val employeeResult = employeeTask.await()
        val recordsResult = recordsTask.await()

        if (employeeResult.isFailure || recordsResult.isFailure) {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                records = listOf(),
                employee = ViewEmployeeUIModel.EmployeeUIModel("", "", EmployeePK("")),
            )
        } else {
            employee = employeeResult.getOrNull()
            _uiState.value = ViewEmployeeUIState(
                false,
                employeeResult.getOrThrow().toUIModel(),
                recordsResult.getOrThrow().map { it.toUIModel() },
            )
        }
    }

    fun share(timeCardRecordPK: TimeCardRecordPK) = viewModelScope.launch {
        _uiState.value = _uiState.value.copy(isLoading = true)
        val state = uiState.value
        val record = state.records.find { it.timeCardRecordPK.documentPath == timeCardRecordPK.documentPath }
        if (record == null) {
            _uiState.value = _uiState.value.copy(isLoading = false)
            return@launch
        }

        val imageUri = record.imageRed?.let {
            val res = storageService.downloadImage(StorageRef(it))

            if (res.isSuccess) {
                res.getOrNull()
            } else {
                null
            }
        }
        _event.emit(ViewEmployeeEvent.TriggerMainActivityEvent(
            MainActivityEvent.ShareContent(
                formatShareMessage(employee, record.timeRecorded, record.eventType),
                imageUri,
            )
        ))
        _uiState.value = _uiState.value.copy(isLoading = false)
    }

    fun onClockEventSelected(eventType: TimeCardEventType) = viewModelScope.launch {
        this@ViewEmployeeViewModel.eventType = eventType
        // TODO: Set the filename
        _event.emit(ViewEmployeeEvent.TriggerMainActivityEvent(
            MainActivityEvent.OpenCamera(eventType.toString())
        ))
    }

    fun recordClockEvent(photoUri: Uri) = viewModelScope.launch {
        val timeCardEventType = eventType ?: return@launch

        _uiState.value = _uiState.value.copy(isLoading = true)

        val newRecord = TimeCardRecordModel(
            id = TimeCardRecordPK(""),
            employeePk = employee!!.employeePK,
            eventType = timeCardEventType,
            eventTime = clock.now().epochSeconds,
            imageUrl = null,
        )
        timeCardManager.addRecord(
            newRecord,
            cachedImageUrl = photoUri,
        ).onSuccess {
            _event.emit(ViewEmployeeEvent.TriggerMainActivityEvent(
                MainActivityEvent.ShareContent(
                    formatShareMessage(
                        employee,
                        newRecord.eventTime.toFriendlyDateTime(),
                        eventType.eventTypeFriendlyName()
                    ),
                    photoUri,
                )
            ))
            eventType = null
            loadEmployee(employee!!.employeePK)
        }.onFailure { throwable ->
            throwable.printStackTrace()
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                records = listOf(),
                employee = ViewEmployeeUIModel.EmployeeUIModel("", "", EmployeePK("")),
            )
            eventType = null
        }
    }

    private fun formatShareMessage(employee: EmployeeModel?, localDateTime: String, timeCardEventType: String): String {
        // TODO: refactor this to use string resources
        return "$timeCardEventType de ${employee?.fullName()}\n$localDateTime"
    }
}

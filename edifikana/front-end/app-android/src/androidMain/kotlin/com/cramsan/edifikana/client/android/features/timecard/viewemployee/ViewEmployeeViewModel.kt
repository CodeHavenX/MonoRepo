package com.cramsan.edifikana.client.android.features.timecard.viewemployee

import android.content.Context
import android.net.Uri
import com.cramsan.edifikana.client.android.R
import com.cramsan.edifikana.client.android.features.base.EdifikanaBaseViewModel
import com.cramsan.edifikana.client.android.features.main.MainActivityEvent
import com.cramsan.edifikana.client.android.managers.EmployeeManager
import com.cramsan.edifikana.client.android.managers.StorageService
import com.cramsan.edifikana.client.android.managers.TimeCardManager
import com.cramsan.edifikana.client.android.models.EmployeeModel
import com.cramsan.edifikana.client.android.models.TimeCardRecordModel
import com.cramsan.edifikana.client.android.models.fullName
import com.cramsan.edifikana.client.lib.eventTypeFriendlyName
import com.cramsan.edifikana.client.lib.toFriendlyDateTime
import com.cramsan.edifikana.lib.firestore.EmployeePK
import com.cramsan.edifikana.lib.firestore.TimeCardEventType
import com.cramsan.edifikana.lib.firestore.TimeCardRecordPK
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import javax.inject.Inject

@HiltViewModel
class ViewEmployeeViewModel @Inject constructor(
    private val employeeManager: EmployeeManager,
    private val timeCardManager: TimeCardManager,
    private val storageService: StorageService,
    private val clock: Clock,
    @ApplicationContext
    private val context: Context,
    exceptionHandler: CoroutineExceptionHandler,
) : EdifikanaBaseViewModel(exceptionHandler) {

    private val _uiState = MutableStateFlow(
        ViewEmployeeUIState(
            true,
            null,
            emptyList(),
            context.getString(R.string.title_timecard_view_employee)
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
            employee = employeeResult.getOrThrow()
            _uiState.value = ViewEmployeeUIState(
                false,
                employeeResult.getOrThrow().toUIModel(),
                recordsResult.getOrThrow().map { it.toUIModel() },
                context.getString(R.string.title_timecard_view_employee)
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

        val imageUri = record.imageRef?.let {
            val res = storageService.downloadImage(it)

            if (res.isSuccess) {
                res.getOrThrow()
            } else {
                null
            }
        }
        _event.emit(
            ViewEmployeeEvent.TriggerMainActivityEvent(
                MainActivityEvent.ShareContent(
                    formatShareMessage(employee, record.timeRecorded, record.eventType),
                    imageUri,
                )
            )
        )
        _uiState.value = _uiState.value.copy(isLoading = false)
    }

    fun onClockEventSelected(eventType: TimeCardEventType) = viewModelScope.launch {
        this@ViewEmployeeViewModel.eventType = eventType
        // TODO: Set the filename
        _event.emit(
            ViewEmployeeEvent.TriggerMainActivityEvent(
                MainActivityEvent.OpenCamera(eventType.toString())
            )
        )
    }

    fun recordClockEvent(photoUri: Uri) = viewModelScope.launch {
        val timeCardEventType = eventType ?: return@launch
        val employee = employee ?: return@launch

        _uiState.value = _uiState.value.copy(isLoading = true)

        val newRecord = TimeCardRecordModel(
            id = TimeCardRecordPK(""),
            employeePk = employee.employeePK,
            eventType = timeCardEventType,
            eventTime = clock.now().epochSeconds,
            imageUrl = null,
            imageRef = null,
        )
        val result = timeCardManager.addRecord(
            newRecord,
            cachedImageUrl = photoUri,
        )

        if (result.isFailure) {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                records = listOf(),
                employee = ViewEmployeeUIModel.EmployeeUIModel("", "", EmployeePK("")),
            )
            eventType = null
        } else {
            _event.emit(
                ViewEmployeeEvent.TriggerMainActivityEvent(
                    MainActivityEvent.ShareContent(
                        formatShareMessage(
                            employee,
                            newRecord.eventTime.toFriendlyDateTime(),
                            eventType.eventTypeFriendlyName()
                        ),
                        photoUri,
                    )
                )
            )
            eventType = null
            loadEmployee(employee.employeePK)
        }
    }

    private fun formatShareMessage(employee: EmployeeModel?, localDateTime: String, timeCardEventType: String): String {
        // TODO: refactor this to use string resources
        return "$timeCardEventType de ${employee?.fullName()}\n$localDateTime"
    }
}

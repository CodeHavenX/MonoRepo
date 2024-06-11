package com.cramsan.edifikana.client.lib.features.timecard.viewemployee

import com.cramsan.edifikana.client.lib.eventTypeFriendlyName
import com.cramsan.edifikana.client.lib.features.base.EdifikanaBaseViewModel
import com.cramsan.edifikana.client.lib.features.main.MainActivityEvent
import com.cramsan.edifikana.client.lib.managers.EmployeeManager
import com.cramsan.edifikana.client.lib.managers.TimeCardManager
import com.cramsan.edifikana.client.lib.models.EmployeeModel
import com.cramsan.edifikana.client.lib.models.TimeCardRecordModel
import com.cramsan.edifikana.client.lib.models.fullName
import com.cramsan.edifikana.client.lib.service.StorageService
import com.cramsan.edifikana.client.lib.toFriendlyDateTime
import com.cramsan.edifikana.lib.firestore.EmployeePK
import com.cramsan.edifikana.lib.firestore.TimeCardEventType
import com.cramsan.edifikana.lib.firestore.TimeCardRecordPK
import com.cramsan.framework.core.CoreUri
import com.cramsan.framework.core.DispatcherProvider
import com.cramsan.framework.logging.logW
import edifikana_lib.Res
import edifikana_lib.error_message_currently_uploading
import edifikana_lib.title_timecard_view_employee
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.jetbrains.compose.resources.getString
import kotlin.random.Random

class ViewEmployeeViewModel(
    private val employeeManager: EmployeeManager,
    private val timeCardManager: TimeCardManager,
    private val storageService: StorageService,
    private val clock: Clock,
    exceptionHandler: CoroutineExceptionHandler,
    dispatcherProvider: DispatcherProvider,
) : EdifikanaBaseViewModel(exceptionHandler, dispatcherProvider) {

    private val _uiState = MutableStateFlow(
        ViewEmployeeUIState(
            true,
            null,
            emptyList(),
            "",
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
                getString(Res.string.title_timecard_view_employee)
            )
        }
    }

    fun share(timeCardRecordPK: TimeCardRecordPK?) = viewModelScope.launch {
        if (timeCardRecordPK == null) {
            logW(TAG, "TimeCardRecord PK is null")
            _event.emit(
                ViewEmployeeEvent.TriggerMainActivityEvent(
                    MainActivityEvent.ShowSnackbar(getString(Res.string.error_message_currently_uploading))
                )
            )
            return@launch
        }

        _uiState.value = _uiState.value.copy(isLoading = true)
        val state = uiState.value
        val record = state.records.find { it.timeCardRecordPK?.documentPath == timeCardRecordPK.documentPath }
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

    fun recordClockEvent(photoUri: CoreUri) = viewModelScope.launch {
        val timeCardEventType = eventType ?: return@launch
        val employee = employee ?: return@launch
        val employeePk = employee.employeePK ?: return@launch

        _uiState.value = _uiState.value.copy(isLoading = true)

        val newRecord = TimeCardRecordModel(
            id = null,
            entityId = Random.nextInt().toString(),
            employeePk = employeePk,
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
            loadEmployee(employeePk)
        }
    }

    private fun formatShareMessage(employee: EmployeeModel?, localDateTime: String, timeCardEventType: String): String {
        // TODO: refactor this to use string resources
        return "$timeCardEventType de ${employee?.fullName()}\n$localDateTime"
    }

    companion object {
        private const val TAG = "ViewEmployeeViewModel"
    }
}

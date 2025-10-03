package com.cramsan.edifikana.client.lib.features.management.viewemployee

import com.cramsan.edifikana.client.lib.eventTypeFriendlyName
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.EmployeeManager
import com.cramsan.edifikana.client.lib.managers.PropertyManager
import com.cramsan.edifikana.client.lib.managers.TimeCardManager
import com.cramsan.edifikana.client.lib.models.EmployeeModel
import com.cramsan.edifikana.client.lib.models.TimeCardRecordModel
import com.cramsan.edifikana.client.lib.models.fullName
import com.cramsan.edifikana.client.lib.service.StorageService
import com.cramsan.edifikana.client.lib.toFriendlyDateTime
import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.TimeCardEventId
import com.cramsan.edifikana.lib.model.TimeCardEventType
import com.cramsan.framework.core.CoreUri
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.core.compose.resources.StringProvider
import com.cramsan.framework.logging.logW
import com.cramsan.framework.utils.time.Chronos
import edifikana_lib.Res
import edifikana_lib.error_message_currently_uploading
import edifikana_lib.title_timecard_view_employee
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlin.random.Random
import kotlin.time.ExperimentalTime

/**
 * View model for viewing employee.
 */
@OptIn(ExperimentalTime::class)
class ViewEmployeeViewModel(
    private val employeeManager: EmployeeManager,
    private val timeCardManager: TimeCardManager,
    private val storageService: StorageService,
    private val propertyManager: PropertyManager,
    private val stringProvider: StringProvider,
    dependencies: ViewModelDependencies,
) : BaseViewModel<ViewEmployeeEvent, ViewEmployeeUIState>(dependencies, ViewEmployeeUIState.Empty, TAG) {

    private var eventType: TimeCardEventType? = null
    private var employee: EmployeeModel? = null

    /**
     * Load employee member.
     */
    fun loadEmployee(employeePK: EmployeeId) = viewModelScope.launch {
        updateUiState { it.copy(isLoading = true) }

        val employeeTask = async { employeeManager.getEmployee(employeePK) }
        val recordsTask = async { timeCardManager.getRecords(employeePK) }

        val employeeResult = employeeTask.await()
        val recordsResult = recordsTask.await()

        if (employeeResult.isFailure || recordsResult.isFailure) {
            updateUiState {
                it.copy(
                    isLoading = false,
                    records = listOf(),
                    employee = ViewEmployeeUIModel.EmployeeUIModel("", "", EmployeeId("")),
                )
            }
        } else {
            employee = employeeResult.getOrThrow()
            val uiState = ViewEmployeeUIState(
                false,
                employeeResult.getOrThrow().toUIModel(stringProvider),
                recordsResult.getOrThrow().map { it.toUIModel(stringProvider) },
                stringProvider.getString(Res.string.title_timecard_view_employee)
            )
            updateUiState { uiState }
        }
    }

    /**
     * Share a time card record.
     */
    fun share(timeCardRecordPK: TimeCardEventId?) = viewModelScope.launch {
        if (timeCardRecordPK == null) {
            logW(TAG, "TimeCardRecord PK is null")
            emitWindowEvent(
                EdifikanaWindowsEvent.ShowSnackbar(
                    stringProvider.getString(Res.string.error_message_currently_uploading)
                )
            )
            return@launch
        }

        updateUiState { it.copy(isLoading = true) }
        val state = uiState.value
        val record = state.records.find { it.timeCardRecordPK?.timeCardEventId == timeCardRecordPK.timeCardEventId }
        if (record == null) {
            updateUiState { it.copy(isLoading = false) }
            return@launch
        }

        val imageUri = record.publicImageUrl?.let {
            storageService.downloadFile(it).getOrThrow()
        }
        emitWindowEvent(
            EdifikanaWindowsEvent.ShareContent(
                formatShareMessage(employee, record.timeRecorded, record.eventType),
                imageUri,
            )
        )
        updateUiState { it.copy(isLoading = false) }
    }

    /**
     * Record a clock event.
     */
    fun onClockEventSelected(eventType: TimeCardEventType) = viewModelScope.launch {
        this@ViewEmployeeViewModel.eventType = eventType
        // TODO: Set the filename
        emitWindowEvent(
            EdifikanaWindowsEvent.OpenCamera(eventType.toString())
        )
    }

    /**
     * Record a clock event.
     */
    fun recordClockEvent(photoUri: CoreUri) = viewModelScope.launch {
        val timeCardEventType = eventType ?: return@launch
        val employee = this@ViewEmployeeViewModel.employee ?: return@launch
        val employeePk = employee.id ?: return@launch
        val propertyId = propertyManager.activeProperty().value?.propertyId ?: return@launch

        updateUiState { it.copy(isLoading = true) }

        val newRecord = TimeCardRecordModel(
            id = null,
            entityId = Random.nextInt().toString(),
            employeePk = employeePk,
            propertyId = PropertyId(propertyId),
            eventType = timeCardEventType,
            eventTime = Chronos.currentInstant().epochSeconds,
            imageUrl = null,
            imageRef = null,
        )
        val result = timeCardManager.addRecord(
            newRecord,
            cachedImageUrl = photoUri,
        )

        if (result.isFailure) {
            updateUiState {
                it.copy(
                    isLoading = false,
                    records = listOf(),
                    employee = ViewEmployeeUIModel.EmployeeUIModel("", "", EmployeeId("")),
                )
            }
            eventType = null
        } else {
            emitWindowEvent(
                EdifikanaWindowsEvent.ShareContent(
                    formatShareMessage(
                        employee,
                        newRecord.eventTime.toFriendlyDateTime(),
                        eventType.eventTypeFriendlyName(stringProvider)
                    ),
                    photoUri,
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

    /**
     * Navigate back.
     */
    fun navigateBack() {
        viewModelScope.launch {
            emitWindowEvent(EdifikanaWindowsEvent.NavigateBack)
        }
    }

    companion object {
        private const val TAG = "ViewEmployeeViewModel"
    }
}

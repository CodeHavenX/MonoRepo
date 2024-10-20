package com.cramsan.edifikana.client.lib.features.timecard.viewstaff

import com.cramsan.edifikana.client.lib.eventTypeFriendlyName
import com.cramsan.edifikana.client.lib.features.base.EdifikanaBaseViewModel
import com.cramsan.edifikana.client.lib.features.main.MainActivityEvent
import com.cramsan.edifikana.client.lib.managers.StaffManager
import com.cramsan.edifikana.client.lib.managers.TimeCardManager
import com.cramsan.edifikana.client.lib.models.StaffModel
import com.cramsan.edifikana.client.lib.models.TimeCardRecordModel
import com.cramsan.edifikana.client.lib.models.fullName
import com.cramsan.edifikana.client.lib.service.StorageService
import com.cramsan.edifikana.client.lib.toFriendlyDateTime
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.edifikana.lib.model.TimeCardEventId
import com.cramsan.edifikana.lib.model.TimeCardEventType
import com.cramsan.framework.core.CoreUri
import com.cramsan.framework.core.DispatcherProvider
import com.cramsan.framework.logging.logW
import edifikana_lib.Res
import edifikana_lib.error_message_currently_uploading
import edifikana_lib.title_timecard_view_staff
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

/**
 * View model for viewing staff.
 */
class ViewStaffViewModel(
    private val staffManager: StaffManager,
    private val timeCardManager: TimeCardManager,
    private val storageService: StorageService,
    private val clock: Clock,
    exceptionHandler: CoroutineExceptionHandler,
    dispatcherProvider: DispatcherProvider,
) : EdifikanaBaseViewModel(exceptionHandler, dispatcherProvider) {

    private val _uiState = MutableStateFlow(
        ViewStaffUIState(
            true,
            null,
            emptyList(),
            "",
        )
    )

    /**
     * UI state flow.
     */
    val uiState: StateFlow<ViewStaffUIState> = _uiState

    private val _event = MutableSharedFlow<ViewStaffEvent>()

    /**
     * Event flow.
     */
    val event: SharedFlow<ViewStaffEvent> = _event

    private var eventType: TimeCardEventType? = null
    private var staff: StaffModel? = null

    /**
     * Load staff member.
     */
    fun loadStaff(staffPK: StaffId) = viewModelScope.launch {
        _uiState.value = _uiState.value.copy(isLoading = true)

        val staffTask = async { staffManager.getStaff(staffPK) }
        val recordsTask = async { timeCardManager.getRecords(staffPK) }

        val staffResult = staffTask.await()
        val recordsResult = recordsTask.await()

        if (staffResult.isFailure || recordsResult.isFailure) {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                records = listOf(),
                staff = ViewStaffUIModel.StaffUIModel("", "", StaffId("")),
            )
        } else {
            staff = staffResult.getOrThrow()
            _uiState.value = ViewStaffUIState(
                false,
                staffResult.getOrThrow().toUIModel(),
                recordsResult.getOrThrow().map { it.toUIModel() },
                getString(Res.string.title_timecard_view_staff)
            )
        }
    }

    /**
     * Share a time card record.
     */
    fun share(timeCardRecordPK: TimeCardEventId?) = viewModelScope.launch {
        if (timeCardRecordPK == null) {
            logW(TAG, "TimeCardRecord PK is null")
            _event.emit(
                ViewStaffEvent.TriggerMainActivityEvent(
                    MainActivityEvent.ShowSnackbar(getString(Res.string.error_message_currently_uploading))
                )
            )
            return@launch
        }

        _uiState.value = _uiState.value.copy(isLoading = true)
        val state = uiState.value
        val record = state.records.find { it.timeCardRecordPK?.timeCardEventId == timeCardRecordPK.timeCardEventId }
        if (record == null) {
            _uiState.value = _uiState.value.copy(isLoading = false)
            return@launch
        }

        val imageUri = record.imageRef?.let {
            storageService.downloadImage(it).getOrThrow()
        }
        _event.emit(
            ViewStaffEvent.TriggerMainActivityEvent(
                MainActivityEvent.ShareContent(
                    formatShareMessage(staff, record.timeRecorded, record.eventType),
                    imageUri,
                )
            )
        )
        _uiState.value = _uiState.value.copy(isLoading = false)
    }

    /**
     * Record a clock event.
     */
    fun onClockEventSelected(eventType: TimeCardEventType) = viewModelScope.launch {
        this@ViewStaffViewModel.eventType = eventType
        // TODO: Set the filename
        _event.emit(
            ViewStaffEvent.TriggerMainActivityEvent(
                MainActivityEvent.OpenCamera(eventType.toString())
            )
        )
    }

    /**
     * Record a clock event.
     */
    fun recordClockEvent(photoUri: CoreUri) = viewModelScope.launch {
        val timeCardEventType = eventType ?: return@launch
        val staff = staff ?: return@launch
        val staffPk = staff.id ?: return@launch

        _uiState.value = _uiState.value.copy(isLoading = true)

        val newRecord = TimeCardRecordModel(
            id = null,
            entityId = Random.nextInt().toString(),
            staffPk = staffPk,
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
                staff = ViewStaffUIModel.StaffUIModel("", "", StaffId("")),
            )
            eventType = null
        } else {
            _event.emit(
                ViewStaffEvent.TriggerMainActivityEvent(
                    MainActivityEvent.ShareContent(
                        formatShareMessage(
                            staff,
                            newRecord.eventTime.toFriendlyDateTime(),
                            eventType.eventTypeFriendlyName()
                        ),
                        photoUri,
                    )
                )
            )
            eventType = null
            loadStaff(staffPk)
        }
    }

    private fun formatShareMessage(staff: StaffModel?, localDateTime: String, timeCardEventType: String): String {
        // TODO: refactor this to use string resources
        return "$timeCardEventType de ${staff?.fullName()}\n$localDateTime"
    }

    companion object {
        private const val TAG = "ViewStaffViewModel"
    }
}

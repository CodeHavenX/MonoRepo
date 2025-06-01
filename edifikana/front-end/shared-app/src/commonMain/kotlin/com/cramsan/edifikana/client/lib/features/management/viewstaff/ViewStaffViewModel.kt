package com.cramsan.edifikana.client.lib.features.management.viewstaff

import com.cramsan.edifikana.client.lib.eventTypeFriendlyName
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.PropertyManager
import com.cramsan.edifikana.client.lib.managers.StaffManager
import com.cramsan.edifikana.client.lib.managers.TimeCardManager
import com.cramsan.edifikana.client.lib.models.StaffModel
import com.cramsan.edifikana.client.lib.models.TimeCardRecordModel
import com.cramsan.edifikana.client.lib.models.fullName
import com.cramsan.edifikana.client.lib.service.StorageService
import com.cramsan.edifikana.client.lib.toFriendlyDateTime
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.StaffId
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
import edifikana_lib.title_timecard_view_staff
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * View model for viewing staff.
 */
class ViewStaffViewModel(
    private val staffManager: StaffManager,
    private val timeCardManager: TimeCardManager,
    private val storageService: StorageService,
    private val propertyManager: PropertyManager,
    private val stringProvider: StringProvider,
    dependencies: ViewModelDependencies,
) : BaseViewModel<ViewStaffEvent, ViewStaffUIState>(dependencies, ViewStaffUIState.Empty, TAG) {

    private var eventType: TimeCardEventType? = null
    private var staff: StaffModel? = null

    /**
     * Load staff member.
     */
    fun loadStaff(staffPK: StaffId) = viewModelScope.launch {
        updateUiState { it.copy(isLoading = true) }

        val staffTask = async { staffManager.getStaff(staffPK) }
        val recordsTask = async { timeCardManager.getRecords(staffPK) }

        val staffResult = staffTask.await()
        val recordsResult = recordsTask.await()

        if (staffResult.isFailure || recordsResult.isFailure) {
            updateUiState {
                it.copy(
                    isLoading = false,
                    records = listOf(),
                    staff = ViewStaffUIModel.StaffUIModel("", "", StaffId("")),
                )
            }
        } else {
            staff = staffResult.getOrThrow()
            val uiState = ViewStaffUIState(
                false,
                staffResult.getOrThrow().toUIModel(stringProvider),
                recordsResult.getOrThrow().map { it.toUIModel(stringProvider) },
                stringProvider.getString(Res.string.title_timecard_view_staff)
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
                formatShareMessage(staff, record.timeRecorded, record.eventType),
                imageUri,
            )
        )
        updateUiState { it.copy(isLoading = false) }
    }

    /**
     * Record a clock event.
     */
    fun onClockEventSelected(eventType: TimeCardEventType) = viewModelScope.launch {
        this@ViewStaffViewModel.eventType = eventType
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
        val staff = staff ?: return@launch
        val staffPk = staff.id ?: return@launch
        val propertyId = propertyManager.activeProperty().value?.propertyId ?: return@launch

        updateUiState { it.copy(isLoading = true) }

        val newRecord = TimeCardRecordModel(
            id = null,
            entityId = Random.nextInt().toString(),
            staffPk = staffPk,
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
                    staff = ViewStaffUIModel.StaffUIModel("", "", StaffId("")),
                )
            }
            eventType = null
        } else {
            emitWindowEvent(
                EdifikanaWindowsEvent.ShareContent(
                    formatShareMessage(
                        staff,
                        newRecord.eventTime.toFriendlyDateTime(),
                        eventType.eventTypeFriendlyName(stringProvider)
                    ),
                    photoUri,
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

    /**
     * Navigate back.
     */
    fun navigateBack() {
        viewModelScope.launch {
            emitWindowEvent(EdifikanaWindowsEvent.NavigateBack)
        }
    }

    companion object {
        private const val TAG = "ViewStaffViewModel"
    }
}

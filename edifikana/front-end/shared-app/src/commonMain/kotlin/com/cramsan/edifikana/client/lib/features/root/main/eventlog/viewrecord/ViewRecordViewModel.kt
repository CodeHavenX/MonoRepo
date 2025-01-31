package com.cramsan.edifikana.client.lib.features.root.main.eventlog.viewrecord

import com.cramsan.edifikana.client.lib.features.root.EdifikanaApplicationEvent
import com.cramsan.edifikana.client.lib.features.root.main.MainActivityEvent
import com.cramsan.edifikana.client.lib.managers.EventLogManager
import com.cramsan.edifikana.client.lib.models.AttachmentHolder
import com.cramsan.edifikana.client.lib.models.EventLogRecordModel
import com.cramsan.edifikana.client.lib.service.StorageService
import com.cramsan.edifikana.lib.model.EventLogEntryId
import com.cramsan.framework.assertlib.assertFalse
import com.cramsan.framework.core.CoreUri
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import edifikana_lib.Res
import edifikana_lib.title_event_log_view
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

/**
 * Represents the UI state of the View Record screen.
 */
class ViewRecordViewModel(
    private val eventLogManager: EventLogManager,
    private val storageService: StorageService,
    dependencies: ViewModelDependencies,
) : BaseViewModel(dependencies) {

    private val _uiState = MutableStateFlow(
        ViewRecordUIState(null, true, "")
    )

    /**
     * UI state flow.
     */
    val uiState: StateFlow<ViewRecordUIState> = _uiState

    private val _event = MutableSharedFlow<ViewRecordEvent>()

    /**
     * Event flow.
     */
    val event: SharedFlow<ViewRecordEvent> = _event

    private var record: EventLogRecordModel? = null

    /**
     * Load a record.
     */
    fun loadRecord(eventLogRecord: EventLogEntryId) = viewModelScope.launch {
        _uiState.value = _uiState.value.copy(isLoading = true)

        val result = eventLogManager.getRecord(eventLogRecord)

        if (result.isFailure) {
            _uiState.value = ViewRecordUIState(
                null,
                false,
                getString(Res.string.title_event_log_view)
            )
        } else {
            val loadedRecord = result.getOrThrow()
            this@ViewRecordViewModel.record = loadedRecord
            _uiState.value = ViewRecordUIState(
                loadedRecord.toUIModel(),
                isLoading = false,
                getString(Res.string.title_event_log_view),
            )
        }
    }

    /**
     * Delete a record.
     */
    fun share() = viewModelScope.launch {
        val record = (uiState.value as? ViewRecordUIState)?.record ?: return@launch

        // TODO: Move this to a resource
        val message = "${record.title}\n" +
            "${record.eventType}\n" +
            "${record.timeRecorded}\n" +
            "Dpto: ${record.unit}\n" +
            record.description
        _event.emit(
            ViewRecordEvent.TriggerMainActivityEvent(
                MainActivityEvent.TriggerApplicationEvent(
                    EdifikanaApplicationEvent.ShowSnackbar(message)
                )
            )
        )
    }

    /**
     * Delete a record.
     */
    fun pickMultipleVisualMedia() = viewModelScope.launch {
        _event.emit(
            ViewRecordEvent.TriggerMainActivityEvent(
                MainActivityEvent.TriggerApplicationEvent(
                    EdifikanaApplicationEvent.OpenPhotoPicker()
                )
            )
        )
    }

    /**
     * Delete a record.
     */
    fun upload(uris: List<CoreUri>) = viewModelScope.launch {
        val recordPk = record?.id ?: return@launch

        _uiState.value = _uiState.value.copy(isLoading = true)
        _uiState.value = _uiState.value.copy(isLoading = false)

        assertFalse(uris.isNotEmpty(), TAG, "No URIs to upload")

        loadRecord(recordPk)
    }

    /**
     * Delete a record.
     */
    fun openImage(attachmentHolder: AttachmentHolder) = viewModelScope.launch {
        val storageRef = attachmentHolder.storageRef

        val imageUri = if (storageRef != null) {
            val res = storageService.downloadFile(storageRef)

            if (res.isSuccess) {
                res.getOrThrow()
            } else {
                null
            } ?: return@launch
        } else {
            CoreUri.createUri(attachmentHolder.publicUrl)
        }

        _event.emit(
            ViewRecordEvent.TriggerMainActivityEvent(
                MainActivityEvent.TriggerApplicationEvent(
                    EdifikanaApplicationEvent.OpenImageExternally(imageUri)
                )
            )
        )
    }

    companion object {
        private const val TAG = "ViewRecordViewModel"
    }
}

package com.cramsan.edifikana.client.lib.features.eventlog.viewrecord

import com.cramsan.edifikana.client.lib.features.base.EdifikanaBaseViewModel
import com.cramsan.edifikana.client.lib.features.main.MainActivityEvent
import com.cramsan.edifikana.client.lib.managers.AttachmentManager
import com.cramsan.edifikana.client.lib.managers.EventLogManager
import com.cramsan.edifikana.client.lib.models.AttachmentHolder
import com.cramsan.edifikana.client.lib.models.EventLogRecordModel
import com.cramsan.edifikana.client.lib.service.StorageService
import com.cramsan.edifikana.lib.EventLogRecordPK
import com.cramsan.framework.core.CoreUri
import com.cramsan.framework.core.DispatcherProvider
import edifikana_lib.Res
import edifikana_lib.title_event_log_view
import kotlinx.coroutines.CoroutineExceptionHandler
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
    private val attachmentManager: AttachmentManager,
    private val storageService: StorageService,
    exceptionHandler: CoroutineExceptionHandler,
    dispatcherProvider: DispatcherProvider,
) : EdifikanaBaseViewModel(exceptionHandler, dispatcherProvider) {

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
    fun loadRecord(eventLogRecord: EventLogRecordPK) = viewModelScope.launch {
        _uiState.value = _uiState.value.copy(isLoading = true)

        val result = eventLogManager.getRecord(eventLogRecord)

        if (result.isFailure) {
            _uiState.value = ViewRecordUIState(null, false, getString(Res.string.title_event_log_view))
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
        val message = "${record.summary}\n" +
            "${record.eventType}\n" +
            "${record.timeRecorded}\n" +
            "Dpto: ${record.unit}\n" +
            record.description
        _event.emit(
            ViewRecordEvent.TriggerMainActivityEvent(
                MainActivityEvent.ShareContent(
                    text = message,
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
                MainActivityEvent.OpenPhotoPicker()
            )
        )
    }

    /**
     * Delete a record.
     */
    fun upload(uris: List<CoreUri>) = viewModelScope.launch {
        val recordPk = record?.id ?: return@launch

        _uiState.value = _uiState.value.copy(isLoading = true)
        attachmentManager.addAttachment(uris, recordPk)
        _uiState.value = _uiState.value.copy(isLoading = false)

        loadRecord(recordPk)
    }

    /**
     * Delete a record.
     */
    fun openImage(attachmentHolder: AttachmentHolder) = viewModelScope.launch {
        val storageRef = attachmentHolder.storageRef

        val imageUri = if (storageRef != null) {
            val res = storageService.downloadImage(storageRef)

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
                MainActivityEvent.OpenImageExternally(imageUri)
            )
        )
    }
}

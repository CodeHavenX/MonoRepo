package com.cramsan.edifikana.client.lib.features.home.viewrecord

import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowDelegatedEvent
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.EventLogManager
import com.cramsan.edifikana.client.lib.models.AttachmentHolder
import com.cramsan.edifikana.client.lib.models.EventLogRecordModel
import com.cramsan.edifikana.client.lib.service.StorageService
import com.cramsan.edifikana.lib.model.EventLogEntryId
import com.cramsan.framework.assertlib.assertFalse
import com.cramsan.framework.core.CoreUri
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.EventEmitter
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.core.compose.resources.StringProvider
import edifikana_lib.Res
import edifikana_lib.title_event_log_view
import kotlinx.coroutines.launch

/**
 * Represents the UI state of the View Record screen.
 */
class ViewRecordViewModel(
    private val eventLogManager: EventLogManager,
    private val storageService: StorageService,
    private val stringProvider: StringProvider,
    private val delegatedEventEmitter: EventEmitter<EdifikanaWindowDelegatedEvent>,
    dependencies: ViewModelDependencies,
) : BaseViewModel<ViewRecordEvent, ViewRecordUIState>(dependencies, ViewRecordUIState.Empty, TAG) {

    private var record: EventLogRecordModel? = null

    init {
        viewModelScope.launch {
            delegatedEventEmitter.events.collect { event ->
                when (event) {
                    is EdifikanaWindowDelegatedEvent.HandleReceivedImages -> {
                        upload(event.uris)
                    }
                    else -> Unit
                }
            }
        }
    }

    /**
     * Load a record.
     */
    fun loadRecord(eventLogRecord: EventLogEntryId) = viewModelScope.launch {
        updateUiState { it.copy(isLoading = true) }

        val result = eventLogManager.getRecord(eventLogRecord)

        if (result.isFailure) {
            val state = ViewRecordUIState(
                null,
                false,
                stringProvider.getString(Res.string.title_event_log_view)
            )
            updateUiState { state }
        } else {
            val loadedRecord = result.getOrThrow()
            this@ViewRecordViewModel.record = loadedRecord
            val state = ViewRecordUIState(
                loadedRecord.toUIModel(stringProvider),
                isLoading = false,
                stringProvider.getString(Res.string.title_event_log_view),
            )
            updateUiState { state }
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
        emitWindowEvent(
            EdifikanaWindowsEvent.ShowSnackbar(message)
        )
    }

    /**
     * Delete a record.
     */
    fun pickMultipleVisualMedia() = viewModelScope.launch {
        emitWindowEvent(
            EdifikanaWindowsEvent.OpenPhotoPicker
        )
    }

    /**
     * Delete a record.
     */
    private fun upload(uris: List<CoreUri>) = viewModelScope.launch {
        val recordPk = record?.id ?: return@launch

        updateUiState { it.copy(isLoading = true) }
        updateUiState { it.copy(isLoading = false) }

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

        emitWindowEvent(
            EdifikanaWindowsEvent.OpenImageExternally(imageUri)
        )
    }

    /**
     * Delete a record.
     */
    fun navigateBack() {
        viewModelScope.launch {
            emitWindowEvent(
                EdifikanaWindowsEvent.NavigateBack
            )
        }
    }

    companion object {
        private const val TAG = "ViewRecordViewModel"
    }
}

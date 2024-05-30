package com.cramsan.edifikana.client.android.features.eventlog.viewrecord

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.cramsan.edifikana.client.android.features.base.EdifikanaBaseViewModel
import com.cramsan.edifikana.client.android.features.main.MainActivityEvent
import com.cramsan.edifikana.client.android.managers.AttachmentManager
import com.cramsan.edifikana.client.android.managers.EventLogManager
import com.cramsan.edifikana.client.android.managers.StorageService
import com.cramsan.edifikana.client.android.models.AttachmentHolder
import com.cramsan.edifikana.client.android.models.EventLogRecordModel
import com.cramsan.edifikana.lib.firestore.EventLogRecordPK
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewRecordViewModel @Inject constructor(
    private val eventLogManager: EventLogManager,
    private val attachmentManager: AttachmentManager,
    private val storageService: StorageService,
    exceptionHandler: CoroutineExceptionHandler,
) : EdifikanaBaseViewModel(exceptionHandler) {

    private val _uiState = MutableStateFlow(ViewRecordUIState(null, true))
    val uiState: StateFlow<ViewRecordUIState> = _uiState

    private val _event = MutableSharedFlow<ViewRecordEvent>()
    val event: SharedFlow<ViewRecordEvent> = _event

    private var record: EventLogRecordModel? = null

    fun loadRecord(eventLogRecord: EventLogRecordPK) = viewModelScope.launch {
        _uiState.value = _uiState.value.copy(isLoading = true)

        val result = eventLogManager.getRecord(eventLogRecord)

        if (result.isFailure) {
            _uiState.value = ViewRecordUIState(null, false)
        } else {
            val loadedRecord = result.getOrThrow()
            this@ViewRecordViewModel.record = loadedRecord
            _uiState.value = ViewRecordUIState(
                loadedRecord.toUIModel(),
                isLoading = false,
            )
        }
    }

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

    fun pickMultipleVisualMedia() = viewModelScope.launch {
        _event.emit(
            ViewRecordEvent.TriggerMainActivityEvent(
                MainActivityEvent.OpenPhotoPicker()
            )
        )
    }

    fun upload(uris: List<Uri>) = viewModelScope.launch {
        val recordPk = record?.id ?: return@launch

        _uiState.value = _uiState.value.copy(isLoading = true)
        attachmentManager.addAttachment(uris, recordPk)
        _uiState.value = _uiState.value.copy(isLoading = false)

        loadRecord(recordPk)
    }

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
            Uri.parse(attachmentHolder.publicUrl)
        }

        _event.emit(
            ViewRecordEvent.TriggerMainActivityEvent(
                MainActivityEvent.OpenImageExternally(imageUri)
            )
        )
    }
}

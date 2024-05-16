package com.cramsan.edifikana.client.android.features.eventlog.viewrecord

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cramsan.edifikana.client.android.features.main.MainActivityEvent
import com.cramsan.edifikana.client.android.managers.AttachmentManager
import com.cramsan.edifikana.client.android.managers.EventLogManager
import com.cramsan.edifikana.client.android.managers.StorageService
import com.cramsan.edifikana.client.android.models.EventLogRecordModel
import com.cramsan.edifikana.client.android.models.StorageRef
import com.cramsan.edifikana.lib.firestore.EventLogRecordPK
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ViewRecordViewModel @Inject constructor(
    private val eventLogManager: EventLogManager,
    private val attachmentManager: AttachmentManager,
    private val storageService: StorageService,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ViewRecordUIState(null, true))
    val uiState: StateFlow<ViewRecordUIState> = _uiState

    private val _event = MutableSharedFlow<ViewRecordEvent>()
    val event: SharedFlow<ViewRecordEvent> = _event

    private var record: EventLogRecordModel? = null

    fun loadRecord(eventLogRecord: EventLogRecordPK) = viewModelScope.launch {
        _uiState.value = _uiState.value.copy(isLoading = true)

        val result = eventLogManager.getRecord(eventLogRecord)

        result.onSuccess { record ->
            this@ViewRecordViewModel.record = record
            _uiState.value = ViewRecordUIState(
                record.toUIModel(),
                isLoading = false,
            )
        }.onFailure {
            _uiState.value = ViewRecordUIState(null, false)
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
        _event.emit(ViewRecordEvent.TriggerMainActivityEvent(
            MainActivityEvent.ShareContent(
                text = message,
            )
        ))
    }

    fun pickMultipleVisualMedia() = viewModelScope.launch {
        _event.emit(ViewRecordEvent.TriggerMainActivityEvent(
            MainActivityEvent.OpenPhotoPicker()
        ))
    }

    fun upload(uris: List<Uri>) = viewModelScope.launch {
        val recordPk = record?.id ?: return@launch

        _uiState.value = _uiState.value.copy(isLoading = true)
        attachmentManager.addAttachment(uris, recordPk)
        _uiState.value = _uiState.value.copy(isLoading = false)

        loadRecord(recordPk)
    }

    fun openImage(imageRef: String) = viewModelScope.launch {
        val storageRef = StorageRef(imageRef)
        val res = storageService.downloadImage(storageRef)

        val imageUri = if (res.isSuccess) {
            res.getOrNull()
        } else {
            null
        } ?: return@launch

        _event.emit(ViewRecordEvent.TriggerMainActivityEvent(
            MainActivityEvent.OpenImageExternally(imageUri)
        ))
    }
}

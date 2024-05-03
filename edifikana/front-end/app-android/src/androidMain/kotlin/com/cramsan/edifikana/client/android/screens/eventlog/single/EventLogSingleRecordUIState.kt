package com.cramsan.edifikana.client.android.screens.eventlog.single

import android.net.Uri
import com.cramsan.edifikana.client.android.utils.toFriendlyDateTime
import com.cramsan.edifikana.lib.firestore.EventLogRecord
import com.cramsan.edifikana.lib.firestore.EventLogRecordPK
import com.cramsan.edifikana.lib.firestore.EventType

sealed class EventLogSingleRecordUIState {

    data object Loading : EventLogSingleRecordUIState()

    data class Success(
        val record: EventLogRecordUIModel,
        val shareMessage: String,
        val imageUri: Uri?,
    ) : EventLogSingleRecordUIState()

    data class Error(val messageRes: Int) : EventLogSingleRecordUIState()
}

data class EventLogRecordUIModel (
    val summary: String,
    val description: String,
    val eventType: String,
    val unit: String,
    val timeRecorded: String,
    val imageUri: Uri?,
    val recordPK: EventLogRecordPK,
)

fun EventLogRecord.toUIModel(): EventLogRecordUIModel {
    return EventLogRecordUIModel(
        summary = summary.orEmpty(),
        description = description.orEmpty(),
        eventType = eventType.toFriendlyString(),
        unit = unit.orEmpty(),
        timeRecorded = timeRecorded.toFriendlyDateTime(),
        imageUri = null,
        recordPK = documentId(),
    )
}

fun EventType?.toFriendlyString(): String {
    return when (this) {
        EventType.GUEST -> return "Invitado"
        EventType.DELIVERY -> return "Cargo/Delivery"
        EventType.INCIDENT -> return "Incidente/Seguridad"
        EventType.MAINTENANCE_SERVICE -> return "Mantenimiento/Servicio"
        else -> return "Otro"
    }
}

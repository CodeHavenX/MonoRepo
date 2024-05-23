package com.cramsan.edifikana.client.android.managers.mappers

import com.cramsan.edifikana.client.android.db.models.EventLogRecordEntity
import com.cramsan.edifikana.client.android.models.AttachmentHolder
import com.cramsan.edifikana.client.android.models.EventLogRecordModel
import com.cramsan.edifikana.client.android.models.StorageRef
import com.cramsan.edifikana.client.android.utils.publicDownloadUrl
import com.cramsan.edifikana.lib.firestore.EmployeePK
import com.cramsan.edifikana.lib.firestore.EventLogRecord
import com.cramsan.edifikana.lib.firestore.EventLogRecordPK
import com.cramsan.edifikana.lib.firestore.EventType
import com.cramsan.edifikana.lib.firestore.FireStoreModel


fun EventLogRecordModel.toEntity(): EventLogRecordEntity {
    return EventLogRecordEntity(
        id.documentPath,
        employeePk?.documentPath,
        timeRecorded,
        unit,
        eventType,
        fallbackEmployeeName,
        fallbackEventType,
        summary,
        description,
    )
}

fun EventLogRecordEntity.toDomainModel(): EventLogRecordModel {
    return EventLogRecordModel(
        id = EventLogRecordPK(id),
        employeePk = employeeDocumentId?.let { EmployeePK(it) },
        timeRecorded = timeRecorded ?: TODO("Time recorded cannot be null"),
        unit = unit.orEmpty(),
        eventType = eventType ?: EventType.OTHER,
        fallbackEmployeeName = fallbackEmployeeName,
        fallbackEventType = fallbackEventType,
        summary = summary.orEmpty(),
        description = description.orEmpty(),
        attachments = emptyList(),
    )
}

@FireStoreModel
fun EventLogRecord.toDomainModel(storageBucket: String): EventLogRecordModel {
    return EventLogRecordModel(
        id = documentId(),
        employeePk = employeeDocumentId?.let { EmployeePK(it) },
        timeRecorded = timeRecorded ?: TODO("Time recorded cannot be null"),
        unit = unit.orEmpty(),
        eventType = eventType ?: EventType.OTHER,
        fallbackEmployeeName = fallbackEmployeeName,
        fallbackEventType = fallbackEventType,
        summary = summary.orEmpty(),
        description = description.orEmpty(),
        attachments = attachments?.map {
            AttachmentHolder(
                publicUrl = publicDownloadUrl(StorageRef(it), storageBucket),
                storageRef = StorageRef(it),
            )
        } ?: emptyList(),
    )
}

@FireStoreModel
fun EventLogRecordModel.toFirebaseModel(): EventLogRecord {
    return EventLogRecord(
        employeeDocumentId = employeePk?.documentPath,
        timeRecorded = timeRecorded,
        unit = unit,
        eventType = eventType,
        fallbackEmployeeName = fallbackEmployeeName,
        fallbackEventType = fallbackEventType,
        summary = summary,
        description = description,
        attachments = attachments.mapNotNull { it.storageRef?.ref },
    )
}

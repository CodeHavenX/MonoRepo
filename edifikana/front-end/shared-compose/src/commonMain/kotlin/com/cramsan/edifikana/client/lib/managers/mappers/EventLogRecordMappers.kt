package com.cramsan.edifikana.client.lib.managers.mappers

import com.cramsan.edifikana.client.lib.db.models.EventLogRecordEntity
import com.cramsan.edifikana.client.lib.models.AttachmentHolder
import com.cramsan.edifikana.client.lib.models.EventLogRecordModel
import com.cramsan.edifikana.client.lib.models.StorageRef
import com.cramsan.edifikana.client.lib.utils.publicDownloadUrl
import com.cramsan.edifikana.lib.EmployeePK
import com.cramsan.edifikana.lib.firestore.EventLogRecord
import com.cramsan.edifikana.lib.firestore.EventType
import com.cramsan.edifikana.lib.firestore.FireStoreModel

fun EventLogRecordModel.toEntity(): EventLogRecordEntity {
    return EventLogRecordEntity(
        requireNotNull(entityId),
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
        id = null,
        entityId = id,
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
        entityId = null,
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
        }.orEmpty(),
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

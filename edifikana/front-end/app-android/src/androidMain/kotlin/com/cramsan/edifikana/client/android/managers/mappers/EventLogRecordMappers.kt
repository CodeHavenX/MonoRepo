package com.cramsan.edifikana.client.android.managers.mappers

import com.cramsan.edifikana.client.android.db.models.EventLogRecordEntity
import com.cramsan.edifikana.client.android.models.EventLogRecordModel
import com.cramsan.edifikana.client.android.models.StorageRef
import com.cramsan.edifikana.client.android.utils.publicDownloadUrl
import com.cramsan.edifikana.lib.firestore.EmployeePK
import com.cramsan.edifikana.lib.firestore.EventLogRecord
import com.cramsan.edifikana.lib.firestore.EventLogRecordPK
import com.cramsan.edifikana.lib.firestore.FireStoreModel


fun EventLogRecordModel.toEntity(): EventLogRecordEntity {
    return EventLogRecordEntity(
        id.documentPath,
        employeePk.documentPath,
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
        employeePk = EmployeePK(employeeDocumentId ?: TODO("Employee document ID cannot be null")),
        timeRecorded = timeRecorded ?: TODO("Time recorded cannot be null"),
        unit = unit ?: TODO("Unit cannot be null"),
        eventType = eventType ?: TODO("Event type cannot be null"),
        fallbackEmployeeName = fallbackEmployeeName,
        fallbackEventType = fallbackEventType,
        summary = summary ?: TODO("Summary cannot be null"),
        description = description ?: TODO("Description cannot be null"),
        attachments = emptyList(),
    )
}

@OptIn(FireStoreModel::class)
fun EventLogRecord.toDomainModel(storageBucket: String): EventLogRecordModel {
    return EventLogRecordModel(
        id = documentId(),
        employeePk = EmployeePK(employeeDocumentId ?: TODO("Employee document ID cannot be null")),
        timeRecorded = timeRecorded ?: TODO("Time recorded cannot be null"),
        unit = unit ?: TODO("Unit cannot be null"),
        eventType = eventType ?: TODO("Event type cannot be null"),
        fallbackEmployeeName = fallbackEmployeeName,
        fallbackEventType = fallbackEventType,
        summary = summary ?: TODO("Summary cannot be null"),
        description = description ?: TODO("Description cannot be null"),
        attachments = attachments?.map {
            publicDownloadUrl(StorageRef(it), storageBucket)
        } ?: emptyList(),
    )
}

@OptIn(FireStoreModel::class)
fun EventLogRecordModel.toFirebaseModel(): EventLogRecord {
    return EventLogRecord(
        employeeDocumentId = employeePk.documentPath,
        timeRecorded = timeRecorded,
        unit = unit,
        eventType = eventType,
        fallbackEmployeeName = fallbackEmployeeName,
        fallbackEventType = fallbackEventType,
        summary = summary,
        description = description,
        attachments = attachments,
    )
}

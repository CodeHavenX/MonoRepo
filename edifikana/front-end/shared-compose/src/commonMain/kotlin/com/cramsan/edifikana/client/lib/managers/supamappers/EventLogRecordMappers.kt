package com.cramsan.edifikana.client.lib.managers.supamappers

import com.cramsan.edifikana.client.lib.models.AttachmentHolder
import com.cramsan.edifikana.client.lib.models.EventLogRecordModel
import com.cramsan.edifikana.client.lib.models.StorageRef
import com.cramsan.edifikana.client.lib.utils.publicDownloadUrl
import com.cramsan.edifikana.lib.EmployeePK
import com.cramsan.edifikana.lib.EventLogRecordPK
import com.cramsan.edifikana.lib.firestore.EventType
import com.cramsan.edifikana.lib.supa.EventLogRecord
import com.cramsan.edifikana.lib.supa.SupabaseModel

@SupabaseModel
fun EventLogRecord.toDomainModel(storageBucket: String): EventLogRecordModel {
    return EventLogRecordModel(
        id = EventLogRecordPK(pk),
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

@SupabaseModel
fun EventLogRecordModel.toFirebaseModel(): EventLogRecord {
    return EventLogRecord.create(
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

package com.cramsan.edifikana.client.lib.managers.mappers

import com.cramsan.edifikana.client.lib.db.models.EventLogRecordEntity
import com.cramsan.edifikana.client.lib.models.EventLogRecordModel
import com.cramsan.edifikana.lib.EmployeePK
import com.cramsan.edifikana.lib.model.EventLogEventType

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
        eventType = eventType ?: EventLogEventType.OTHER,
        fallbackEmployeeName = fallbackEmployeeName,
        fallbackEventType = fallbackEventType,
        summary = summary.orEmpty(),
        description = description.orEmpty(),
        attachments = emptyList(),
    )
}

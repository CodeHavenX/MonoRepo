package com.cramsan.edifikana.client.lib.managers.mappers

import com.cramsan.edifikana.client.lib.db.models.EventLogRecordEntity
import com.cramsan.edifikana.client.lib.models.EventLogRecordModel
import com.cramsan.edifikana.lib.model.employee.EmployeeId
import com.cramsan.edifikana.lib.model.eventLog.EventLogEventType
import kotlin.time.Instant

/**
 * Convert an event log record model to an entity.
 */
fun EventLogRecordModel.toEntity(): EventLogRecordEntity {
    return EventLogRecordEntity(
        requireNotNull(entityId),
        employeePk?.empId,
        timeRecorded.toEpochMilliseconds(),
        unit,
        eventType.name,
        fallbackEmployeeName,
        fallbackEventType,
        title,
        description,
        propertyId = propertyId,
    )
}

/**
 * Convert an event log record entity to a domain model.
 */
fun EventLogRecordEntity.toDomainModel(): EventLogRecordModel {
    return EventLogRecordModel(
        id = null,
        entityId = id,
        employeePk = employeeDocumentId?.let { EmployeeId(it) },
        timeRecorded = timeRecorded?.let { Instant.fromEpochMilliseconds(it) } ?: TODO("Time recorded cannot be null"),
        unit = unit,
        eventType = EventLogEventType.fromString(eventType),
        fallbackEmployeeName = fallbackEmployeeName,
        fallbackEventType = fallbackEventType,
        title = title.orEmpty(),
        description = description.orEmpty(),
        attachments = emptyList(),
        propertyId = propertyId,
    )
}

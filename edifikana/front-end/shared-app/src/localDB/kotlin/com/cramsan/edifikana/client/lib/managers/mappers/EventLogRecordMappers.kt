package com.cramsan.edifikana.client.lib.managers.mappers

import com.cramsan.edifikana.client.lib.db.models.EventLogRecordEntity
import com.cramsan.edifikana.client.lib.models.EventLogRecordModel
import com.cramsan.edifikana.lib.model.EventLogEventType
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.StaffId

/**
 * Convert an event log record model to an entity.
 */
fun EventLogRecordModel.toEntity(): EventLogRecordEntity {
    return EventLogRecordEntity(
        requireNotNull(entityId),
        staffPk?.staffId,
        timeRecorded,
        unit,
        eventType.name,
        fallbackStaffName,
        fallbackEventType,
        title,
        description,
        propertyId = propertyId.propertyId,
    )
}

/**
 * Convert an event log record entity to a domain model.
 */
fun EventLogRecordEntity.toDomainModel(): EventLogRecordModel {
    return EventLogRecordModel(
        id = null,
        entityId = id,
        staffPk = staffDocumentId?.let { StaffId(it) },
        timeRecorded = timeRecorded ?: TODO("Time recorded cannot be null"),
        unit = unit.orEmpty(),
        eventType = EventLogEventType.fromString(eventType),
        fallbackStaffName = fallbackStaffName,
        fallbackEventType = fallbackEventType,
        title = title.orEmpty(),
        description = description.orEmpty(),
        attachments = emptyList(),
        propertyId = PropertyId(propertyId),
    )
}

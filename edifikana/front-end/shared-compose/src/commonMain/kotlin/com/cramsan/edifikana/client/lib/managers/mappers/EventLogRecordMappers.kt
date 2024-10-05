package com.cramsan.edifikana.client.lib.managers.mappers

import com.cramsan.edifikana.client.lib.db.models.EventLogRecordEntity
import com.cramsan.edifikana.client.lib.models.EventLogRecordModel
import com.cramsan.edifikana.lib.StaffPK
import com.cramsan.edifikana.lib.model.EventLogEventType

/**
 * Convert an event log record model to an entity.
 */
fun EventLogRecordModel.toEntity(): EventLogRecordEntity {
    return EventLogRecordEntity(
        requireNotNull(entityId),
        staffPk?.documentPath,
        timeRecorded,
        unit,
        eventType,
        fallbackStaffName,
        fallbackEventType,
        summary,
        description,
    )
}

/**
 * Convert an event log record entity to a domain model.
 */
fun EventLogRecordEntity.toDomainModel(): EventLogRecordModel {
    return EventLogRecordModel(
        id = null,
        entityId = id,
        staffPk = staffDocumentId?.let { StaffPK(it) },
        timeRecorded = timeRecorded ?: TODO("Time recorded cannot be null"),
        unit = unit.orEmpty(),
        eventType = eventType ?: EventLogEventType.OTHER,
        fallbackStaffName = fallbackStaffName,
        fallbackEventType = fallbackEventType,
        summary = summary.orEmpty(),
        description = description.orEmpty(),
        attachments = emptyList(),
    )
}

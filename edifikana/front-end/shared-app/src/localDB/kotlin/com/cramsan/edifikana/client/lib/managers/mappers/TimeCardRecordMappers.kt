package com.cramsan.edifikana.client.lib.managers.mappers

import com.cramsan.edifikana.client.lib.db.models.TimeCardRecordEntity
import com.cramsan.edifikana.client.lib.models.TimeCardRecordModel
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.edifikana.lib.model.TimeCardEventType
import com.cramsan.framework.core.CoreUri

/**
 * Maps a time card record model to a time card record entity.
 */
fun TimeCardRecordModel.toEntity(cachedImageUrl: CoreUri): TimeCardRecordEntity {
    return TimeCardRecordEntity(
        requireNotNull(entityId),
        staffPk.staffId,
        propertyId.propertyId,
        eventType.name,
        eventTime,
        cachedImageUrl.toString(),
    )
}

/**
 * Maps a time card record entity to a time card record model.
 */
fun TimeCardRecordEntity.toDomainModel(): TimeCardRecordModel {
    return TimeCardRecordModel(
        null,
        id,
        StaffId(staffDocumentId ?: TODO("Staff document ID cannot be null")),
        PropertyId(propertyId ?: TODO("Property ID cannot be null")),
        TimeCardEventType.fromString(eventType),
        eventTime ?: TODO("Event time cannot be null"),
        cachedImageUrl,
        null,
    )
}

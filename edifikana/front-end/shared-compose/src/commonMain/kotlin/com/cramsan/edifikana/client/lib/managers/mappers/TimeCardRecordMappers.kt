package com.cramsan.edifikana.client.lib.managers.mappers

import com.cramsan.edifikana.client.lib.db.models.TimeCardRecordEntity
import com.cramsan.edifikana.client.lib.models.TimeCardRecordModel
import com.cramsan.edifikana.lib.StaffPK
import com.cramsan.framework.core.CoreUri

/**
 * Maps a time card record model to a time card record entity.
 */
fun TimeCardRecordModel.toEntity(cachedImageUrl: CoreUri): TimeCardRecordEntity {
    return TimeCardRecordEntity(
        requireNotNull(entityId),
        staffPk.documentPath,
        eventType,
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
        StaffPK(staffDocumentId ?: TODO("Staff document ID cannot be null")),
        eventType ?: TODO("Event type cannot be null"),
        eventTime ?: TODO("Event time cannot be null"),
        cachedImageUrl,
        null,
    )
}

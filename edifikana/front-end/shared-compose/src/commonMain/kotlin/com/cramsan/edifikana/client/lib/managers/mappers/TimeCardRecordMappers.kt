package com.cramsan.edifikana.client.lib.managers.mappers

import com.cramsan.edifikana.client.lib.db.models.TimeCardRecordEntity
import com.cramsan.edifikana.client.lib.models.TimeCardRecordModel
import com.cramsan.edifikana.lib.EmployeePK
import com.cramsan.framework.core.CoreUri

fun TimeCardRecordModel.toEntity(cachedImageUrl: CoreUri): TimeCardRecordEntity {
    return TimeCardRecordEntity(
        requireNotNull(entityId),
        employeePk.documentPath,
        eventType,
        eventTime,
        cachedImageUrl.toString(),
    )
}

fun TimeCardRecordEntity.toDomainModel(): TimeCardRecordModel {
    return TimeCardRecordModel(
        null,
        id,
        EmployeePK(employeeDocumentId ?: TODO("Employee document ID cannot be null")),
        eventType ?: TODO("Event type cannot be null"),
        eventTime ?: TODO("Event time cannot be null"),
        cachedImageUrl,
        null,
    )
}

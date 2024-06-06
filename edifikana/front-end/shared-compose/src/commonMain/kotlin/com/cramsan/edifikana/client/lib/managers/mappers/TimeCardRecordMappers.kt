package com.cramsan.edifikana.client.lib.managers.mappers

import com.cramsan.edifikana.client.lib.db.models.TimeCardRecordEntity
import com.cramsan.edifikana.client.lib.models.StorageRef
import com.cramsan.edifikana.client.lib.models.TimeCardRecordModel
import com.cramsan.edifikana.client.lib.utils.publicDownloadUrl
import com.cramsan.edifikana.lib.firestore.EmployeePK
import com.cramsan.edifikana.lib.firestore.FireStoreModel
import com.cramsan.edifikana.lib.firestore.TimeCardRecord
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

@FireStoreModel
fun TimeCardRecord.toDomainModel(storageBucket: String): TimeCardRecordModel {
    return TimeCardRecordModel(
        id = documentId(),
        entityId = null,
        employeePk = EmployeePK(employeeDocumentId ?: TODO("Employee document ID cannot be null")),
        eventType = eventType ?: TODO("Event type cannot be null"),
        eventTime = eventTime ?: TODO("Event time cannot be null"),
        imageUrl = imageUrl?.let { publicDownloadUrl(StorageRef(it), storageBucket) },
        imageRef = imageUrl?.let { StorageRef(it) },
    )
}

@FireStoreModel
fun TimeCardRecordEntity.toFirebaseModel(): TimeCardRecord {
    return TimeCardRecord(
        employeeDocumentId = employeeDocumentId ?: TODO("Employee document ID cannot be null"),
        eventType = eventType ?: TODO("Event type cannot be null"),
        eventTime = eventTime ?: TODO("Event time cannot be null"),
        imageUrl = cachedImageUrl,
    )
}

package com.cramsan.edifikana.client.android.managers.mappers

import android.net.Uri
import com.cramsan.edifikana.client.android.db.models.TimeCardRecordEntity
import com.cramsan.edifikana.client.android.models.StorageRef
import com.cramsan.edifikana.client.android.models.TimeCardRecordModel
import com.cramsan.edifikana.client.android.utils.publicDownloadUrl
import com.cramsan.edifikana.lib.firestore.EmployeePK
import com.cramsan.edifikana.lib.firestore.FireStoreModel
import com.cramsan.edifikana.lib.firestore.TimeCardRecord
import com.cramsan.edifikana.lib.firestore.TimeCardRecordPK

fun TimeCardRecordModel.toEntity(cachedImageUrl: Uri): TimeCardRecordEntity {
    return TimeCardRecordEntity(
        id.documentPath,
        employeePk.documentPath,
        eventType,
        eventTime,
        cachedImageUrl.toString(),
    )
}

fun TimeCardRecordEntity.toDomainModel(): TimeCardRecordModel {
    return TimeCardRecordModel(
        TimeCardRecordPK(id),
        EmployeePK(employeeDocumentId ?: TODO("Employee document ID cannot be null")),
        eventType ?: TODO("Event type cannot be null"),
        eventTime ?: TODO("Event time cannot be null"),
        cachedImageUrl,
    )
}

@OptIn(FireStoreModel::class)
fun TimeCardRecord.toDomainModel(storageBucket: String): TimeCardRecordModel {
    return TimeCardRecordModel(
        id = documentId(),
        employeePk = EmployeePK(employeeDocumentId ?: TODO("Employee document ID cannot be null")),
        eventType = eventType ?: TODO("Event type cannot be null"),
        eventTime = eventTime ?: TODO("Event time cannot be null"),
        imageUrl = imageUrl?.let { publicDownloadUrl(StorageRef(it), storageBucket) },
    )
}

@OptIn(FireStoreModel::class)
fun TimeCardRecordEntity.toFirebaseModel(): TimeCardRecord {
    return TimeCardRecord(
        employeeDocumentId = employeeDocumentId ?: TODO("Employee document ID cannot be null"),
        eventType = eventType ?: TODO("Event type cannot be null"),
        eventTime = eventTime ?: TODO("Event time cannot be null"),
        imageUrl = cachedImageUrl,
    )
}

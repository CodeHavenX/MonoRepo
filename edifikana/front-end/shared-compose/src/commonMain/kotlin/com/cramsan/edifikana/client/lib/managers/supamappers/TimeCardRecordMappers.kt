package com.cramsan.edifikana.client.lib.managers.supamappers

import com.cramsan.edifikana.client.lib.db.models.TimeCardRecordEntity
import com.cramsan.edifikana.client.lib.models.StorageRef
import com.cramsan.edifikana.client.lib.models.TimeCardRecordModel
import com.cramsan.edifikana.client.lib.utils.publicSupabaseDownloadUrl
import com.cramsan.edifikana.lib.EmployeePK
import com.cramsan.edifikana.lib.TimeCardRecordPK
import com.cramsan.edifikana.lib.supa.SupabaseModel
import com.cramsan.edifikana.lib.supa.TimeCardRecord
import io.github.jan.supabase.storage.Storage

@SupabaseModel
fun TimeCardRecord.toDomainModel(storage: Storage): TimeCardRecordModel {
    return TimeCardRecordModel(
        id = TimeCardRecordPK(pk),
        entityId = null,
        employeePk = EmployeePK(employeeDocumentId ?: TODO("Employee document ID cannot be null")),
        eventType = eventType ?: TODO("Event type cannot be null"),
        eventTime = eventTime ?: TODO("Event time cannot be null"),
        imageUrl = imageUrl?.let { publicSupabaseDownloadUrl(StorageRef(it), storage) },
        imageRef = imageUrl?.let { StorageRef(it) },
    )
}

@SupabaseModel
fun TimeCardRecordEntity.toFirebaseModel(): TimeCardRecord {
    return TimeCardRecord.create(
        employeeDocumentId = employeeDocumentId ?: TODO("Employee document ID cannot be null"),
        eventType = eventType ?: TODO("Event type cannot be null"),
        eventTime = eventTime ?: TODO("Event time cannot be null"),
        imageUrl = cachedImageUrl,
    )
}

@SupabaseModel
fun TimeCardRecordModel.toFirebaseModel(): TimeCardRecord {
    return TimeCardRecord.create(
        employeeDocumentId = employeePk.documentPath,
        eventType = eventType ?: TODO("Event type cannot be null"),
        eventTime = eventTime ?: TODO("Event time cannot be null"),
        imageUrl = imageUrl,
    )
}

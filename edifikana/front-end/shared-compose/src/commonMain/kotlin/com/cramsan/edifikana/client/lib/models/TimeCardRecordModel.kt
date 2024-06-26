package com.cramsan.edifikana.client.lib.models

import com.cramsan.edifikana.lib.firestore.EmployeePK
import com.cramsan.edifikana.lib.firestore.TimeCardEventType
import com.cramsan.edifikana.lib.firestore.TimeCardRecordPK

data class TimeCardRecordModel(
    val id: TimeCardRecordPK?,
    val entityId: String?,
    val employeePk: EmployeePK,
    val eventType: TimeCardEventType,
    val eventTime: Long,
    val imageUrl: String?,
    val imageRef: StorageRef?,
)

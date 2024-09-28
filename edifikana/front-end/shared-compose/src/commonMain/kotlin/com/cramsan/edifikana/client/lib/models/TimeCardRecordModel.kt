package com.cramsan.edifikana.client.lib.models

import com.cramsan.edifikana.lib.EmployeePK
import com.cramsan.edifikana.lib.TimeCardRecordPK
import com.cramsan.edifikana.lib.model.TimeCardEventType

data class TimeCardRecordModel(
    val id: TimeCardRecordPK?,
    val entityId: String?,
    val employeePk: EmployeePK,
    val eventType: TimeCardEventType,
    val eventTime: Long,
    val imageUrl: String?,
    val imageRef: StorageRef?,
)

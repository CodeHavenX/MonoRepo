package com.cramsan.edifikana.client.lib.models

import com.cramsan.edifikana.lib.StaffPK
import com.cramsan.edifikana.lib.TimeCardRecordPK
import com.cramsan.edifikana.lib.model.TimeCardEventType

/**
 * Model for a time card record.
 */
data class TimeCardRecordModel(
    val id: TimeCardRecordPK?,
    val entityId: String?,
    val staffPk: StaffPK,
    val eventType: TimeCardEventType,
    val eventTime: Long,
    val imageUrl: String?,
    val imageRef: StorageRef?,
)

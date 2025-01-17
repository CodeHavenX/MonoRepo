package com.cramsan.edifikana.client.lib.models

import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.edifikana.lib.model.TimeCardEventId
import com.cramsan.edifikana.lib.model.TimeCardEventType

/**
 * Model for a time card record.
 */
data class TimeCardRecordModel(
    val id: TimeCardEventId?,
    val entityId: String?,
    val staffPk: StaffId,
    val propertyId: PropertyId,
    val eventType: TimeCardEventType,
    val eventTime: Long, // TODO: Change to Instant
    val imageUrl: String?,
    val imageRef: String?,
)

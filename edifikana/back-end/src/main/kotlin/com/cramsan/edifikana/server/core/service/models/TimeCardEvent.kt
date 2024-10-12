package com.cramsan.edifikana.server.core.service.models

import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.edifikana.lib.model.TimeCardEventId
import com.cramsan.edifikana.lib.model.TimeCardEventType
import kotlinx.datetime.Instant

/**
 * Domain model representing a time card event such as clocking in or clocking out.
 */
data class TimeCardEvent(
    val id: TimeCardEventId,
    val staffId: StaffId?,
    val fallbackStaffName: String?,
    val propertyId: PropertyId,
    val type: TimeCardEventType,
    val imageUrl: String?,
    val timestamp: Instant,
)

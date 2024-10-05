package com.cramsan.edifikana.server.core.service.models.requests

import com.cramsan.edifikana.lib.model.TimeCardEventType
import com.cramsan.edifikana.server.core.service.models.PropertyId
import com.cramsan.edifikana.server.core.service.models.StaffId
import kotlinx.datetime.Instant

/**
 * Domain model representing a time card event request.
 */
data class CreateTimeCardEventRequest(
    val staffId: StaffId,
    val fallbackStaffName: String?,
    val propertyId: PropertyId,
    val type: TimeCardEventType,
    val imageUrl: String?,
    val timestamp: Instant,
)

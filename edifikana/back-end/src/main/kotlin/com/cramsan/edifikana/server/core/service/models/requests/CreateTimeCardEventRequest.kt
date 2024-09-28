package com.cramsan.edifikana.server.core.service.models.requests

import com.cramsan.edifikana.lib.model.TimeCardEventType
import com.cramsan.edifikana.server.core.service.models.StaffId

/**
 * Domain model representing a time card event request.
 */
data class CreateTimeCardEventRequest(
    val staffId: StaffId,
    val eventType: TimeCardEventType,
)

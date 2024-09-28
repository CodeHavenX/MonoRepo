package com.cramsan.edifikana.server.core.service.models.requests

import com.cramsan.edifikana.server.core.service.models.TimeCardEventId

/**
 * Request to get a time card.
 */
data class GetTimeCardEventRequest(
    val id: TimeCardEventId,
)

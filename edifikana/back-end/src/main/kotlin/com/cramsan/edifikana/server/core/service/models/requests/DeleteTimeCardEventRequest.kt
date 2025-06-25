package com.cramsan.edifikana.server.core.service.models.requests

import com.cramsan.edifikana.lib.model.TimeCardEventId

/**
 * Request to delete a time card.
 */
data class DeleteTimeCardEventRequest(
    val id: TimeCardEventId,
)

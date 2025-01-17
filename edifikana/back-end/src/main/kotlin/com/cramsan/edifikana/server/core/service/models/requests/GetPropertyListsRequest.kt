package com.cramsan.edifikana.server.core.service.models.requests

import com.cramsan.edifikana.lib.model.UserId

/**
 * Request to get a list of properties.
 */
data class GetPropertyListsRequest(
    val userId: UserId,
    val showAll: Boolean,
)

package com.cramsan.edifikana.server.core.service.models.requests

import com.cramsan.edifikana.lib.model.UserId

/**
 * Request to create a property.
 */
data class CreatePropertyRequest(
    val name: String,
    val address: String,
    val creatorUserId: UserId,
)

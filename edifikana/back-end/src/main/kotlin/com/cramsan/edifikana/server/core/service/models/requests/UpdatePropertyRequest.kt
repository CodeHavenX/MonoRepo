package com.cramsan.edifikana.server.core.service.models.requests

import com.cramsan.edifikana.lib.model.PropertyId

/**
 * Request to update a property.
 */
data class UpdatePropertyRequest(
    val propertyId: PropertyId,
    val name: String?,
)

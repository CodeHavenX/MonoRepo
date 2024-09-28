package com.cramsan.edifikana.server.core.service.models.requests

import com.cramsan.edifikana.server.core.service.models.PropertyId

/**
 * Request to get a property.
 */
data class DeletePropertyRequest(
    val propertyId: PropertyId,
)

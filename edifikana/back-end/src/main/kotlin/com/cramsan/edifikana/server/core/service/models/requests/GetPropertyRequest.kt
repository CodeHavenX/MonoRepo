package com.cramsan.edifikana.server.core.service.models.requests

import com.cramsan.edifikana.lib.model.PropertyId

/**
 * Request to get a property.
 */
data class GetPropertyRequest(
    val propertyId: PropertyId,
)

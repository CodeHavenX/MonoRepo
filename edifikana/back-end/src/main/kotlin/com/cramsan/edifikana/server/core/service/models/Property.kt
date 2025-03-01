package com.cramsan.edifikana.server.core.service.models

import com.cramsan.edifikana.lib.model.PropertyId

/**
 * Domain model representing a property.
 */
data class Property(
    val id: PropertyId,
    val name: String,
)

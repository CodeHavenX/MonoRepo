package com.cramsan.edifikana.client.lib.features.management.property

import com.cramsan.edifikana.lib.model.PropertyId

/**
 * UI model for a property.
 */
data class PropertyUIModel(
    val propertyId: PropertyId,
    val name: String,
)

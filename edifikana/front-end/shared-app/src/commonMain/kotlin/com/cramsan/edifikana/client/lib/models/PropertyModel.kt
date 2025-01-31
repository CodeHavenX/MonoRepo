package com.cramsan.edifikana.client.lib.models

import com.cramsan.edifikana.lib.model.PropertyId

/**
 * Model to represent a property.
 */
data class PropertyModel(
    val id: PropertyId,
    val name: String,
    val address: String,
)

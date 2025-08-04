package com.cramsan.edifikana.lib.model

import kotlinx.serialization.Serializable

/**
 * Domain model representing a property ID.
 */
@Serializable
data class PropertyId(val propertyId: String) {
    override fun toString(): String = propertyId
}

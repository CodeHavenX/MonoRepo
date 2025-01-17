package com.cramsan.edifikana.lib.model

/**
 * Domain model representing a property ID.
 */
@JvmInline
value class PropertyId(val propertyId: String) {
    override fun toString(): String = propertyId
}

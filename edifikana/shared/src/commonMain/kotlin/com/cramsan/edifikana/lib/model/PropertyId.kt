package com.cramsan.edifikana.lib.model

import kotlin.jvm.JvmInline

/**
 * Domain model representing a property ID.
 */
@JvmInline
value class PropertyId(val propertyId: String) {
    override fun toString(): String = propertyId
}

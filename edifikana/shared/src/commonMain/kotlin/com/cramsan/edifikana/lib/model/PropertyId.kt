package com.cramsan.edifikana.lib.model

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Domain model representing a property ID.
 */
@JvmInline
@Serializable
value class PropertyId(val propertyId: String) {
    override fun toString(): String = propertyId
}

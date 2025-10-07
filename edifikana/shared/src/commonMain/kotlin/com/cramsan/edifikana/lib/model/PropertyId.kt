package com.cramsan.edifikana.lib.model

import com.cramsan.framework.annotations.api.PathParam
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Domain model representing a property ID.
 */
@JvmInline
@Serializable
value class PropertyId(val propertyId: String) : PathParam {
    override fun toString(): String = propertyId
}

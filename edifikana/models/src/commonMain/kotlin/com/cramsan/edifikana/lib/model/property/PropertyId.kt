package com.cramsan.edifikana.lib.model.property

import com.cramsan.framework.annotations.api.PathParam
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Domain model representing a property ID.
 */
@JvmInline
@Serializable
@JsonSchema.Description("Unique identifier of a property.")
@JsonSchema.Example("\"prop_a1b2c3d4\"")
value class PropertyId(val propertyId: String) : PathParam {
    override fun toString(): String = propertyId
}

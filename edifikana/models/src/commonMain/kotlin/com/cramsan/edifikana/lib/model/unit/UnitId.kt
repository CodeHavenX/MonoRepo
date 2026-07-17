package com.cramsan.edifikana.lib.model.unit

import com.cramsan.framework.annotations.api.PathParam
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Domain model representing a unit ID.
 */
@JvmInline
@Serializable
@JsonSchema.Description("Unique identifier of a unit.")
@JsonSchema.Example("\"unit_a1b2c3d4\"")
value class UnitId(val unitId: String) : PathParam {
    override fun toString(): String = unitId
}

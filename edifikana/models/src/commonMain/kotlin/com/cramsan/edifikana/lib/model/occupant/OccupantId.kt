package com.cramsan.edifikana.lib.model.occupant

import com.cramsan.framework.annotations.api.PathParam
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Domain model representing an occupant ID.
 */
@JvmInline
@Serializable
@JsonSchema.Description("Unique identifier of an occupant.")
@JsonSchema.Example("\"occ_a1b2c3d4\"")
value class OccupantId(val occupantId: String) : PathParam {
    override fun toString(): String = occupantId
}

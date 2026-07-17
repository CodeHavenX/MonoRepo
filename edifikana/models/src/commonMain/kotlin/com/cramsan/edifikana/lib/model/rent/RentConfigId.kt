package com.cramsan.edifikana.lib.model.rent

import com.cramsan.framework.annotations.api.PathParam
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Domain model representing a rent config ID.
 */
@JvmInline
@Serializable
@JsonSchema.Description("Unique identifier of a rent configuration.")
@JsonSchema.Example("\"rc_a1b2c3d4\"")
value class RentConfigId(val rentConfigId: String) : PathParam {
    override fun toString(): String = rentConfigId
}

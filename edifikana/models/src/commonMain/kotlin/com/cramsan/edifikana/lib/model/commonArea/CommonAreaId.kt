package com.cramsan.edifikana.lib.model.commonArea

import com.cramsan.framework.annotations.api.PathParam
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Domain model representing a common area ID.
 */
@JvmInline
@Serializable
@JsonSchema.Description("Unique identifier of a common area.")
@JsonSchema.Example("\"ca_a1b2c3d4\"")
value class CommonAreaId(val commonAreaId: String) : PathParam {
    override fun toString(): String = commonAreaId
}

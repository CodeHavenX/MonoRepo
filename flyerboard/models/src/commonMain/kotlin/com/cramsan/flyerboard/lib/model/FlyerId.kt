package com.cramsan.flyerboard.lib.model

import com.cramsan.framework.annotations.api.PathParam
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Domain model representing a flyer ID.
 */
@JvmInline
@Serializable
@JsonSchema.Description("Unique identifier of a flyer.")
@JsonSchema.Example("\"a1b2c3d4-e5f6-7890-abcd-ef1234567890\"")
value class FlyerId(val flyerId: String) : PathParam {
    override fun toString(): String = flyerId
}

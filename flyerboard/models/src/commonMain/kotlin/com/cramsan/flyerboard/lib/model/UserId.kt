package com.cramsan.flyerboard.lib.model

import com.cramsan.framework.annotations.api.PathParam
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Domain model representing a user ID.
 */
@JvmInline
@Serializable
@JsonSchema.Description("Unique identifier of a user (Supabase Auth UUID).")
@JsonSchema.Example("\"a1b2c3d4-e5f6-7890-abcd-ef1234567890\"")
value class UserId(val userId: String) : PathParam {
    override fun toString(): String = userId
}

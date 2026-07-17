package com.cramsan.edifikana.lib.model.user

import com.cramsan.framework.annotations.api.PathParam
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Domain model representing a user ID.
 */
@JvmInline
@Serializable
@JsonSchema.Description("Unique identifier of a user.")
@JsonSchema.Example("\"usr_a1b2c3d4\"")
value class UserId(val userId: String) : PathParam {
    override fun toString(): String = userId
}

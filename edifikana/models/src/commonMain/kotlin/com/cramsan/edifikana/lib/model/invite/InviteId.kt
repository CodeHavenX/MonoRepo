package com.cramsan.edifikana.lib.model.invite

import com.cramsan.framework.annotations.api.PathParam
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Inline value class representing an Invite ID.
 * This is used to uniquely identify an invite in the system.
 */
@Serializable
@JvmInline
@JsonSchema.Description("Unique identifier of an invite.")
@JsonSchema.Example("\"inv_a1b2c3d4\"")
value class InviteId(val id: String) : PathParam {
    override fun toString(): String = id
}

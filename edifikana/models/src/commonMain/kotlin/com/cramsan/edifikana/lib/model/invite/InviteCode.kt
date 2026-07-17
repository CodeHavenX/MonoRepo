package com.cramsan.edifikana.lib.model.invite

import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Value type representing the redemption code of an invite, as opposed to its [InviteId].
 */
@Serializable
@JvmInline
@JsonSchema.Description("Redemption code of an invite, used by the invitee to join.")
@JsonSchema.Example("\"a1b2c3d4\"")
value class InviteCode(val code: String) {
    override fun toString(): String = code
}

package com.cramsan.edifikana.lib.model

import com.cramsan.framework.annotations.api.PathParam
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Inline value class representing an Invite ID.
 * This is used to uniquely identify an invite in the system.
 */
@Serializable
@JvmInline
value class InviteId(
    val id: String,
) : PathParam {
    override fun toString(): String = id
}

package com.cramsan.edifikana.lib.model

import com.cramsan.framework.annotations.api.PathParam
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Domain model representing an occupant ID.
 */
@JvmInline
@Serializable
value class OccupantId(val occupantId: String) : PathParam {
    override fun toString(): String = occupantId
}

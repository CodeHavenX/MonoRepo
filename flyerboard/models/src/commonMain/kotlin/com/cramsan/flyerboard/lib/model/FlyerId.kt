package com.cramsan.flyerboard.lib.model

import com.cramsan.framework.annotations.api.PathParam
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Domain model representing a flyer ID.
 */
@JvmInline
@Serializable
value class FlyerId(val flyerId: String) : PathParam {
    override fun toString(): String = flyerId
}
